package net.skross04.wildalmanac.ecology.vegetation;

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
}
