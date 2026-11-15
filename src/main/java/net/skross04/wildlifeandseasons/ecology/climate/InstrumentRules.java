package net.skross04.wildlifeandseasons.ecology.climate;

/** The numbers behind the rain gauge. */
public final class InstrumentRules {
    /** The rain gauge holds 15 steps of water (comparator 0–15). */
    public static final int GAUGE_MAX = 15;

    private InstrumentRules() {
    }

    /** How much water a check adds while it rains on the gauge: more in a thunderstorm. */
    public static int rainAdded(boolean thundering) {
        return thundering ? 2 : 1;
    }

    /**
     * Chance per check that the gauge loses a step of water when it's dry.
     * Frozen water doesn't evaporate; the warmer it is, the faster it goes.
     */
    public static double evaporationChance(double temperature) {
        if (temperature <= 0) {
            return 0.0;
        }
        return Math.min(0.5, 0.05 + temperature / 60.0);
    }

    /** Fill level as a percentage. */
    public static int fillPercent(int fill) {
        return Math.round(fill * 100.0F / GAUGE_MAX);
    }
}
