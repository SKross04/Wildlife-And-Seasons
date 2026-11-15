package net.skross04.wildlifeandseasons.climate;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.skross04.wildlifeandseasons.ecology.climate.SnowAndIce;

/**
 * Spring thaw: snow layers and ice that are open to the sky (or only under tree leaves) melt when the day is warm
 * enough. Covered ice and snow (igloos, under a roof, ice storage) are left alone, like vanilla's freezing rule.
 */
public final class SeasonalThaw {
    private SeasonalThaw() {
    }

    /** Called from the ice and snow-layer random ticks (see the mixins). */
    public static boolean shouldMelt(ServerLevel level, BlockPos pos, RandomSource random) {
        if (!isOpenToSkyOrCanopy(level, pos)) {
            return false;
        }
        double dailyMean = WorldClimate.dailyMeanAt(level, pos);
        if (Double.isNaN(dailyMean)) {
            return false; // no seasons here: vanilla behaviour
        }
        return random.nextDouble() < SnowAndIce.meltChance(dailyMean);
    }

    /**
     * Nothing above this block that stops rain, or only a tree's leaves (snow also lies under trees in forests).
     * A real roof (igloo, house, ice storage) keeps snow and ice from melting.
     */
    public static boolean isOpenToSkyOrCanopy(ServerLevel level, BlockPos pos) {
        BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);
        if (top.getY() <= pos.getY() + 1) {
            return true;
        }
        return level.getBlockState(top.below()).getBlock() instanceof LeavesBlock;
    }
}
