package net.skross04.wildlifeandseasons.ecology.farming;

import java.util.Set;
import java.util.function.BooleanSupplier;

import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.season.Season;

/**
 * When a seasonal crop is allowed to grow.
 * <ul>
 *     <li>In one of its seasons: grows normally.</li>
 *     <li>Out of season: stops completely (never destroyed, it just waits), unless it's in a greenhouse.</li>
 *     <li>Tropical zones have no cold season, so crops grow there all year. Places without seasons (Nether, End) too.</li>
 * </ul>
 */
public final class CropSeasonRules {
    private CropSeasonRules() {
    }

    /**
     * @param growSeasons the seasons this crop grows in
     * @param current     the current season
     * @param zone        climate zone where the crop stands
     * @param greenhouse  whether the crop is under a glass roof; only asked when it matters (the check costs a bit)
     */
    public static boolean isInSeason(Set<Season> growSeasons, Season current, ClimateZone zone, BooleanSupplier greenhouse) {
        if (growSeasons.contains(current)) {
            return true;
        }
        if (!zone.hasSeasons() || zone == ClimateZone.TROPICAL) {
            return true;
        }
        return greenhouse.getAsBoolean();
    }

    /**
     * Whether an out-of-season crop still grows this time, for servers that set a softer rule in the config.
     *
     * @param outOfSeasonGrowth 0 = never (default), 0.25 = a quarter of the normal speed, 1 = seasons don't matter
     * @param random            a random number from 0 to 1
     */
    public static boolean growsAnyway(double outOfSeasonGrowth, double random) {
        return random < outOfSeasonGrowth;
    }
}
