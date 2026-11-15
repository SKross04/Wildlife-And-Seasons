package net.skross04.wildalmanac.nature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.skross04.wildalmanac.Config;
import net.skross04.wildalmanac.block.ModBlocks;
import net.skross04.wildalmanac.climate.WorldClimate;
import net.skross04.wildalmanac.ecology.climate.IcicleRules;

/**
 * Icicles: they start under snowy roof edges in winter (snow above, open air below), grow up to three blocks
 * long, drip when the sun warms them and melt in spring or next to a torch, like ice.
 * Icicles a player placed are left alone (see {@code IcicleBlock#PERSISTENT}).
 */
public final class Icicles {
    private Icicles() {
    }

    /** Called when a snow layer gets a random tick (see {@code SnowLayerBlockMixin}). */
    public static void onSnowRandomTick(ServerLevel level, BlockPos snowPos, RandomSource random) {
        if (!Config.icicles() || random.nextDouble() >= IcicleRules.FORM_CHANCE) {
            return;
        }
        BlockPos roof = snowPos.below();
        BlockPos hang = roof.below();
        BlockState roofState = level.getBlockState(roof);
        if (roofState.is(BlockTags.LEAVES) || !level.getBlockState(hang).isAir()
                || !roofState.isFaceSturdy(level, roof, Direction.DOWN)) {
            return; // only under solid overhangs, not under trees or on the ground
        }
        if (IcicleRules.canGrow(WorldClimate.dailyMeanAt(level, hang))) {
            level.setBlockAndUpdate(hang, ModBlocks.ICICLE.get().defaultBlockState());
        }
    }

    public static void onIcicleRandomTick(ServerLevel level, BlockPos pos, RandomSource random) {
        double dailyMean = WorldClimate.dailyMeanAt(level, pos);
        if (!Config.icicles() || level.getBrightness(LightLayer.BLOCK, pos) > 11
                || random.nextDouble() < IcicleRules.meltChance(dailyMean)) {
            level.removeBlock(pos, false); // melts, like ice next to a torch
            return;
        }
        BlockPos below = pos.below();
        if (IcicleRules.canGrow(dailyMean) && random.nextDouble() < IcicleRules.GROW_CHANCE
                && level.getBlockState(below).isAir() && length(level, pos) < IcicleRules.MAX_LENGTH) {
            level.setBlockAndUpdate(below, ModBlocks.ICICLE.get().defaultBlockState());
        }
    }

    /** Client: the tip of an icicle drips when it's above freezing (sunny winter afternoons, the spring thaw). */
    public static void drip(Level level, BlockPos pos, RandomSource random) {
        if (level.getBlockState(pos.below()).is(ModBlocks.ICICLE.get()) || random.nextFloat() >= 0.3F) {
            return;
        }
        double temperature = WorldClimate.temperatureNow(level.getBiome(pos).value(), pos);
        if (!Double.isNaN(temperature) && temperature > 0) {
            level.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, 0, 0, 0);
        }
    }

    /** How many icicle blocks hang together, counting up from this one. */
    private static int length(Level level, BlockPos pos) {
        int length = 1;
        BlockPos up = pos.above();
        while (length <= IcicleRules.MAX_LENGTH && level.getBlockState(up).is(ModBlocks.ICICLE.get())) {
            length++;
            up = up.above();
        }
        return length;
    }
}
