package net.skross04.wildalmanac.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.skross04.wildalmanac.climate.Instruments;
import net.skross04.wildalmanac.ecology.climate.InstrumentRules;

/**
 * A glass tube on a stake that collects rain. It fills up while it rains on it (faster in a thunderstorm)
 * and slowly dries out on warm dry days; frozen, it keeps its water. A comparator reads how full it is (0–15).
 * Right-click to read it, together with how rainy this season is here.
 */
public class RainGaugeBlock extends Block {
    public static final IntegerProperty FILL = IntegerProperty.create("fill", 0, InstrumentRules.GAUGE_MAX);
    private static final VoxelShape SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 14.0, 10.0);
    private static final int CHECK_INTERVAL = 200;

    public RainGaugeBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FILL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FILL);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level instanceof ServerLevel serverLevel) {
            player.sendOverlayMessage(Instruments.rainGaugeReading(serverLevel, pos, state.getValue(FILL)));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(this)) {
            level.scheduleTick(pos, this, CHECK_INTERVAL);
        }
    }

    /** Every 10 seconds: rain in, or a little evaporation out. */
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int fill = state.getValue(FILL);
        int next = Instruments.nextGaugeFill(level, pos, fill, random);
        if (next != fill) {
            level.setBlock(pos, state.setValue(FILL, next), Block.UPDATE_ALL);
            level.updateNeighbourForOutputSignal(pos, this);
        }
        level.scheduleTick(pos, this, CHECK_INTERVAL);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return state.getValue(FILL);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }
}
