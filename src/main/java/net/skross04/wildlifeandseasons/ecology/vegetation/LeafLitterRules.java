package net.skross04.wildlifeandseasons.ecology.vegetation;

/**
 * How fast fallen leaves gather on a forest floor and rot away again.
 * <p>
 * Checked every time Minecraft randomly ticks a grass block under trees (about 17 times per in-game day
 * per block at the normal random tick speed). Leaf litter has 1–4 layers.
 * <ul>
 *     <li>Autumn: a layer is added now and then, most at the peak of autumn.
 *     About half the forest floor is covered by mid-autumn, most of it (about 2 layers) by winter.</li>
 *     <li>Winter: the litter stays (under the snow).</li>
 *     <li>Spring and summer: it rots away, one layer at a time, gone by about mid-spring.</li>
 * </ul>
 */
public final class LeafLitterRules {
    public static final int MAX_LAYERS = 4;
    /** Chance per grass tick to add a layer, at the very peak of autumn. */
    public static final double DROP_CHANCE_AT_PEAK = 0.006;
    /** Chance per grass tick to lose a layer in spring and summer. */
    public static final double ROT_CHANCE = 0.02;

    private LeafLitterRules() {
    }

    /** @param autumn how strongly it's autumn right now, 0–1 (from the seasonal colours) */
    public static double dropChance(double autumn) {
        return DROP_CHANCE_AT_PEAK * autumn;
    }

    /**
     * @param yearPosition 0–1 through the year (spring and summer are the first half)
     * @param autumn       how strongly it's autumn right now; no rotting while leaves are still falling
     */
    public static double rotChance(double yearPosition, double autumn) {
        return yearPosition < 0.5 && autumn < 0.05 ? ROT_CHANCE : 0.0;
    }

    /** How often one grass block gets a random tick per in-game day at the default speed (3 per 16×16×16 section per tick). */
    public static final double GRASS_TICKS_PER_DAY = 3.0 * 24000 / 4096;

    /**
     * The average number of litter layers a spot under deciduous trees has at this time of year
     * (for chunks nobody was near: they catch up to this). Walks through the last two years day by day
     * with the same drop and rot chances as the real thing, so it matches what you'd see in a loaded forest.
     *
     * @param autumnAt how strongly it's autumn at a year position (0–1), from the seasonal colours
     */
    public static double expectedLayers(java.util.function.DoubleUnaryOperator autumnAt, double yearPosition, int yearLength) {
        double layers = 0;
        for (int day = 2 * yearLength; day > 0; day--) {
            double position = yearPosition - (double) day / yearLength;
            position -= Math.floor(position);
            double autumn = autumnAt.applyAsDouble(position);
            double rot = rotChance(position, autumn);
            if (rot > 0) {
                layers = Math.max(0, layers - rot * GRASS_TICKS_PER_DAY);
            } else {
                layers = Math.min(MAX_LAYERS, layers + dropChance(autumn) * GRASS_TICKS_PER_DAY);
            }
        }
        return layers;
    }

    /**
     * Chance that a spot in a chunk that was away for {@code missedDays} catches up all the way
     * (a chunk that missed one day changes only a little, one that missed ten days or more catches up fully).
     */
    public static double catchUpChance(long missedDays) {
        return Math.min(1.0, missedDays / 10.0);
    }
}
