package net.skross04.wildlifeandseasons.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.skross04.wildlifeandseasons.nature.DesertBloom;
import net.skross04.wildlifeandseasons.util.ModTags;

/**
 * A desert wildflower. It grows on sand (and dirt or terracotta), unlike vanilla flowers.
 * Wild ones come up after desert rain and die off again a couple of weeks later (see {@link DesertBloom});
 * ones a player places are {@link #PERSISTENT} and stay.
 */
public class DesertFlowerBlock extends FlowerBlock {
    /** True for flowers placed by a player (like vanilla's player-placed leaves). */
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;

    public DesertFlowerBlock(Holder<MobEffect> suspiciousStewEffect, float seconds, Properties properties) {
        super(suspiciousStewEffect, seconds, properties);
        registerDefaultState(stateDefinition.any().setValue(PERSISTENT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PERSISTENT);
    }

    /** Placed from the item by a player: it stays. (The bloom uses the default state.) */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(PERSISTENT, true);
    }

    /** Sand, red sand, dirt and terracotta (tag {@code wildlifeandseasons:desert_flower_soil}). */
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModTags.Blocks.DESERT_FLOWER_SOIL);
    }

    /** Only wild flowers need random ticks (to die off when the bloom is over). */
    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(PERSISTENT);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        DesertBloom.onWildFlowerRandomTick(level, pos, random);
    }
}
