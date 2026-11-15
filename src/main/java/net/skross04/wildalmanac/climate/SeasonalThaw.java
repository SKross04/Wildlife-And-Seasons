package net.skross04.wildalmanac.climate;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.skross04.wildalmanac.ecology.climate.SnowAndIce;

/**
 * Spring thaw: snow layers and ice that are open to the sky melt when the day is warm enough.
 * Covered ice and snow (igloos, under a roof, ice storage) are left alone, like vanilla's freezing rule.
 */
public final class SeasonalThaw {
    private SeasonalThaw() {
    }

    /** Called from the ice and snow-layer random ticks (see the mixins). */
    public static boolean shouldMelt(ServerLevel level, BlockPos pos, RandomSource random) {
        if (!isOpenToSky(level, pos)) {
            return false;
        }
        double dailyMean = WorldClimate.dailyMeanAt(level, pos);
        if (Double.isNaN(dailyMean)) {
            return false; // no seasons here: vanilla behaviour
        }
        return random.nextDouble() < SnowAndIce.meltChance(dailyMean);
    }

    /** Nothing above this block that stops rain (the same check vanilla uses for where snow and ice form). */
    private static boolean isOpenToSky(ServerLevel level, BlockPos pos) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) <= pos.getY() + 1;
    }
}
