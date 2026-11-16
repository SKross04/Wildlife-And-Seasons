package net.skross04.wildlifeandseasons.ecology.vegetation;

/**
 * The desert bloom: after the rare desert rains, seeds that have waited in the sand for years come up and flower for
 * a short while (real example: Anza-Borrego, California, where desert sunflowers, sand verbena and dune evening
 * primrose carpet the sand in early spring after a wet winter; the more rain, the bigger the bloom).
 * <p>
 * In the game deserts only get rain on a few spring days (see SeasonalWeather), so the bloom comes in spring.
 * A mod day is about 3.6 real days (100-day year), so "a few weeks" is a handful of days.
 */
public final class DesertBloomRules {
    /** Flowers start coming up this many days after the rain (the seeds need a few days to sprout and grow). */
    public static final int SPROUT_DELAY_DAYS = 2;

    /** New flowers keep coming up until this many days after the last rain. */
    public static final int LAST_SPROUT_DAY = 12;

    /** After this many dry days the flowers dry up and die (they've made their seeds). */
    public static final int WILT_AFTER_DAYS = 14;

    /** The summer heat ends any bloom (year position 0.33 ≈ end of June). */
    public static final double HEAT_END = 0.33;

    /** Rainy days in a row of wet spells after which it's a full "superbloom". */
    public static final int SUPERBLOOM_RAIN_DAYS = 3;

    /** A dry gap this long starts a new rain season (old rainy days no longer count). */
    public static final int NEW_SEASON_GAP_DAYS = 25;

    /**
     * Chance per visit of a sand column (about 6 visits per column per day) that a flower comes up there,
     * at full strength in a dense patch. About a quarter of the sand flowers in the best patches.
     */
    public static final double SPROUT_CHANCE = 0.006;

    /** Chance per random tick that a dying flower is gone (gone within about a day). */
    public static final double WILT_CHANCE = 0.25;

    /** Size of the patches (blooms are patchy: washes and dune hollows flower, other places stay bare). */
    public static final int PATCH_SIZE = 8;

    private DesertBloomRules() {
    }

    /** True while new flowers come up: from a couple of days after rain, before the summer heat. */
    public static boolean sprouting(long daysSinceRain, double yearPosition) {
        return daysSinceRain >= SPROUT_DELAY_DAYS && daysSinceRain <= LAST_SPROUT_DAY && beforeHeat(yearPosition);
    }

    /** True when natural desert flowers die off: long dry, summer heat, or no rain known at all. */
    public static boolean wilting(long daysSinceRain, double yearPosition) {
        return daysSinceRain < 0 || daysSinceRain > WILT_AFTER_DAYS || !beforeHeat(yearPosition);
    }

    private static boolean beforeHeat(double yearPosition) {
        double p = yearPosition - Math.floor(yearPosition);
        return p < HEAT_END;
    }

    /** How strong the bloom is, 0..1: one rainy day gives a thin bloom, a few give a superbloom. */
    public static double strength(int rainyDays) {
        return Math.min(1.0, Math.max(0, rainyDays) / (double) SUPERBLOOM_RAIN_DAYS);
    }

    /** How densely this spot's patch flowers, 0.05..1 (most patches thin, some dense). */
    public static double patchDensity(int x, int z) {
        double h = WaterLilyRules.hash(Math.floorDiv(x, PATCH_SIZE), Math.floorDiv(z, PATCH_SIZE), 10);
        return 0.05 + 0.95 * h * h;
    }

    /** Chance that a flower comes up on a visit to this sand column. */
    public static double sproutChance(int rainyDays, int x, int z) {
        return SPROUT_CHANCE * strength(rainyDays) * patchDensity(x, z);
    }

    /**
     * Which of the {@code kinds} flowers grows here: each patch has its main flower (3 in 4 there), the rest mixed in.
     */
    public static int kind(int kinds, int x, int z, double random) {
        int main = (int) (WaterLilyRules.hash(Math.floorDiv(x, PATCH_SIZE), Math.floorDiv(z, PATCH_SIZE), 11) * kinds);
        return random < 0.75 ? main : (int) (random * 4 * kinds) % kinds;
    }
}
