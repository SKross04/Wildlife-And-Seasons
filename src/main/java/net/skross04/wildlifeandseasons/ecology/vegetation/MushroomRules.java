package net.skross04.wildlifeandseasons.ecology.vegetation;

/**
 * When mushrooms come up on the forest floor, and how long they last.
 * <p>
 * Real fungi fruit mostly in autumn: when the soil cools down and there's been rain. Warm weather holds them back early
 * in the season, cold ends it, and the first frosts collapse the mushrooms. Each one lasts only about a week.
 * Bracket fungi on trunks (shelf mushrooms) are tougher: frost only slowly thins them out.
 */
public final class MushroomRules {
    /** Chance per grass tick that a mushroom comes up, in the best weather (cool, frost-free, raining). */
    public static final double FRUIT_CHANCE = 0.0005;
    /** The same for a shelf mushroom on a tree trunk next to the ground. */
    public static final double SHELF_CHANCE = 0.00004;
    /** Chance per grass tick that a shelf mushroom grows from small to big. */
    public static final double SHELF_GROW_CHANCE = 0.02;

    /** A mushroom lasts about this many days, then rots away. */
    public static final double LIFETIME_DAYS = 6;
    /** Chance per grass tick that a mushroom rots away (about {@link #LIFETIME_DAYS} on average). */
    public static final double ROT_CHANCE = 1 / (LIFETIME_DAYS * LeafLitterRules.GRASS_TICKS_PER_DAY);
    /** Chance per grass tick that frost collapses a mushroom: gone within about a day. */
    public static final double FROST_CHANCE = 0.25;
    /** Chance per grass tick that frost kills a shelf mushroom: about a week. */
    public static final double SHELF_FROST_CHANCE = 0.01;

    /** Daily mean temperatures (°C): too warm above, best in between, frost below. */
    private static final double TOO_WARM = 18, WARMEST_BEST = 14, COLDEST_BEST = 5, FROST = 0.5;

    private MushroomRules() {
    }

    /**
     * How strongly mushrooms fruit now, 0–1: only in the second half of the year (late summer to early winter)
     * and only when it's cool but not freezing.
     *
     * @param dailyMean    the day's average temperature here, °C
     * @param yearPosition 0 = start of spring … 1 = end of winter
     */
    public static double fruiting(double dailyMean, double yearPosition) {
        double p = yearPosition - Math.floor(yearPosition);
        double season = smoothstep(0.36, 0.46, p) * (1 - smoothstep(0.80, 0.90, p));
        double temperature;
        if (dailyMean >= TOO_WARM || dailyMean <= FROST) {
            temperature = 0;
        } else if (dailyMean > WARMEST_BEST) {
            temperature = smoothstep(TOO_WARM, WARMEST_BEST, dailyMean);
        } else if (dailyMean < COLDEST_BEST) {
            temperature = smoothstep(FROST, COLDEST_BEST, dailyMean);
        } else {
            temperature = 1;
        }
        return season * temperature;
    }

    /**
     * How wet the ground is for mushrooms, 0–1: most while it rains, still a lot for a couple of days after.
     *
     * @param raining        it's raining now
     * @param daysSinceRain  days since it last rained (negative or huge = unknown, counts as dry)
     */
    public static double wetness(boolean raining, double daysSinceRain) {
        if (raining) {
            return 1.0;
        }
        if (daysSinceRain >= 0 && daysSinceRain <= 1) {
            return 0.6;
        }
        if (daysSinceRain >= 0 && daysSinceRain <= 3) {
            return 0.4;
        }
        return 0.2;
    }

    /** Frost: mushrooms collapse. */
    public static boolean frost(double dailyMean) {
        return dailyMean <= FROST;
    }

    /** Chance a red (fly agaric) instead of a brown mushroom: fly agarics grow with birch and spruce. */
    public static double redChance(boolean underBirchOrSpruce) {
        return underBirchOrSpruce ? 0.5 : 0.2;
    }

    /** Chance a mushroom from before is still there after this many days away (otherwise it has rotted). */
    public static double catchUpSurvival(long missedDays) {
        return Math.pow(1 - ROT_CHANCE, missedDays * LeafLitterRules.GRASS_TICKS_PER_DAY);
    }

    /** Chance a shelf mushroom from before is still there after this many frosty days away. */
    public static double shelfCatchUpSurvival(long frostDays) {
        return Math.pow(1 - SHELF_FROST_CHANCE, frostDays * LeafLitterRules.GRASS_TICKS_PER_DAY);
    }

    private static double smoothstep(double from, double to, double value) {
        double t = Math.max(0, Math.min(1, (value - from) / (to - from)));
        return t * t * (3 - 2 * t);
    }
}
