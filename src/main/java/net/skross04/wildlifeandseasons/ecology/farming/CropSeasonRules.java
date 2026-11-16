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

    /**
     * Growth stages a crop makes per in-season day, on average: a crop gets about 17.6 random ticks a day and
     * grows on roughly 1 in 5 of them on a normal watered farm (vanilla's chance depends on the farmland around it).
     */
    public static final double STAGES_PER_DAY = 3.5;

    /**
     * How many growth stages a crop catches up on, for a chunk nobody was near.
     *
     * @param growingDays days it could grow (in-season days count 1, out-of-season days count the config's growth speed)
     * @param random      0–1, so neighbouring crops don't all end up at exactly the same stage
     */
    public static int catchUpStages(double growingDays, double random) {
        return (int) Math.floor(growingDays * STAGES_PER_DAY * (0.75 + 0.5 * random));
    }

    /** Random ticks one block gets per in-game day at the default random tick speed (3 per 16×16×16 section per tick). */
    public static final double RANDOM_TICKS_PER_DAY = 3.0 * 24000 / 4096;

    /**
     * How many random ticks a plant that grows tick by tick (sugar cane, cactus, bamboo) catches up on.
     *
     * @param growingDays days it could grow (see {@link #catchUpStages})
     * @param random      0–1, so neighbouring plants don't all end up the same
     */
    public static int catchUpTicks(double growingDays, double random) {
        return (int) Math.floor(growingDays * RANDOM_TICKS_PER_DAY * (0.75 + 0.5 * random));
    }
}
