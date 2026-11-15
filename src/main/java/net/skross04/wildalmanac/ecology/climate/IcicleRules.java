package net.skross04.wildalmanac.ecology.climate;

/**
 * When icicles form and melt. Icicles grow where snow on a roof melts a little by day and the water
 * freezes again as it drips over the edge: so in winter, but not in the deepest cold (then nothing melts).
 */
public final class IcicleRules {
    /** °C (today's average): colder than this, no snow melts, so no icicles grow. */
    public static final double COLDEST_FOR_GROWTH = -10.0;
    /** Chance per random tick of a snow layer on an overhang that an icicle starts under it. */
    public static final double FORM_CHANCE = 0.08;
    /** Chance per random tick that an icicle grows one block longer. */
    public static final double GROW_CHANCE = 0.05;
    /** Icicles grow up to this many blocks long. */
    public static final int MAX_LENGTH = 3;

    private IcicleRules() {
    }

    /** Whether icicles can form or grow today. */
    public static boolean canGrow(double dailyMeanTemperature) {
        return !Double.isNaN(dailyMeanTemperature)
                && dailyMeanTemperature >= COLDEST_FOR_GROWTH
                && dailyMeanTemperature <= SnowAndIce.THAW_TEMPERATURE;
    }

    /** Chance per random tick that an icicle melts away (the same thaw as snow and ice). */
    public static double meltChance(double dailyMeanTemperature) {
        return Double.isNaN(dailyMeanTemperature) ? 0.0 : SnowAndIce.meltChance(dailyMeanTemperature);
    }
}
