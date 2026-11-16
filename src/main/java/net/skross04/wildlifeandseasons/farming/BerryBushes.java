package net.skross04.wildlifeandseasons.farming;

import java.util.OptionalDouble;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.skross04.wildlifeandseasons.Config;
import net.skross04.wildlifeandseasons.climate.WorldClimate;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.vegetation.BerryRules;
import net.skross04.wildlifeandseasons.season.SeasonAccess;

/**
 * Sweet berry bushes through the year (see {@link BerryRules}): no new berries before flowering is over, no ripe ones
 * before late summer; last year's berries drop when the bush flowers again. Berries stay on through winter.
 */
public final class BerryBushes {
    private BerryBushes() {
    }

    /** True if a sweet berry bush may grow to this age now (the season's growth rules still apply too). */
    public static boolean mayGrowTo(Level level, BlockPos pos, BlockState state, int newAge) {
        if (!(state.getBlock() instanceof SweetBerryBushBlock) || !Config.seasonalBerries()) {
            return true;
        }
        OptionalDouble yearPosition = SeasonAccess.yearPosition(level);
        if (yearPosition.isEmpty()) {
            return true; // client hasn't heard the season yet: the server decides
        }
        ClimateZone zone = WorldClimate.zoneOf(level.getBiome(pos));
        return BerryRules.allowedAge(zone, yearPosition.getAsDouble(), newAge) >= newAge;
    }

    /** From the bush's random tick: last year's berries drop when it flowers again. True if they did. */
    public static boolean dropOldBerries(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!Config.seasonalBerries() || state.getValue(SweetBerryBushBlock.AGE) < 2) {
            return false;
        }
        ClimateZone zone = WorldClimate.zoneOf(level.getBiome(pos));
        double yearPosition = SeasonAccess.yearPosition(level).orElse(0);
        if (!BerryRules.oldBerriesFall(zone, yearPosition) || random.nextDouble() >= BerryRules.OLD_BERRIES_FALL_CHANCE) {
            return false;
        }
        level.setBlock(pos, state.setValue(SweetBerryBushBlock.AGE, 1), Block.UPDATE_CLIENTS);
        return true;
    }
}
