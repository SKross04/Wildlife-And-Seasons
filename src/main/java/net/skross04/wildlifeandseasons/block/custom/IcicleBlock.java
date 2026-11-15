package net.skross04.wildlifeandseasons.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.skross04.wildlifeandseasons.nature.Icicles;

/**
 * An icicle hanging under a roof edge. It grows in winter, drips when the sun warms it and melts in spring.
 * It hangs from a solid block (or another icicle) and falls apart when that's removed.
 * Silk touch picks it up; icicles a player places are {@link #PERSISTENT}: they never melt or grow, so you can build with them.
 */
public class IcicleBlock extends Block {
    /** True for icicles placed by a player (like vanilla's player-placed leaves). */
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
    private static final VoxelShape SHAPE = Block.box(4.0, 2.0, 4.0, 12.0, 16.0, 12.0);

    public IcicleBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PERSISTENT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PERSISTENT);
    }

    /** Placed from the item by a player: a building block, it stays. (Natural icicles use the default state.) */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(PERSISTENT, true);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);
        return aboveState.is(this) || aboveState.isFaceSturdy(level, above, Direction.DOWN);
    }

    /** When the block it hangs from disappears, the icicle goes too. */
    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == Direction.UP && !canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(PERSISTENT)) {
            Icicles.onIcicleRandomTick(level, pos, random);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(PERSISTENT)) {
            Icicles.drip(level, pos, random);
        }
    }
}
