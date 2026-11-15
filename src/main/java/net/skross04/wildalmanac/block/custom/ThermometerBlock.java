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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.skross04.wildalmanac.climate.Instruments;

/**
 * A garden thermometer on a little stake. Right-click to read the temperature; a comparator reads it as
 * a signal from 0 (-20 °C or colder) to 15 (40 °C or warmer). It checks the temperature every 5 seconds.
 */
public class ThermometerBlock extends Block {
    /** The last measured comparator signal, stored so comparators get told when it changes. */
    public static final IntegerProperty SIGNAL = BlockStateProperties.POWER;
    private static final VoxelShape SHAPE = Block.box(5.0, 0.0, 7.0, 11.0, 16.0, 9.0);
    private static final int CHECK_INTERVAL = 100;

    public ThermometerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(SIGNAL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIGNAL);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level instanceof ServerLevel serverLevel) {
            player.sendOverlayMessage(Instruments.thermometerReading(serverLevel, pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(this)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    /** Every few seconds: measure, and tell comparators when the signal changed. */
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int signal = Instruments.thermometerSignal(level, pos);
        if (signal != state.getValue(SIGNAL)) {
            level.setBlock(pos, state.setValue(SIGNAL, signal), Block.UPDATE_ALL);
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
        return state.getValue(SIGNAL);
    }

    /** Needs solid ground under it, like a torch. */
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
