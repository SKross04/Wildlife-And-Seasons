package net.skross04.wildalmanac.ecology.climate;

/** The numbers behind the thermometer and the rain gauge. */
public final class InstrumentRules {
    /** The thermometer's comparator scale: -20 °C gives signal 0, +40 °C gives 15 (4 °C per step). */
    public static final double SIGNAL_MIN_TEMPERATURE = -20.0;
    public static final double DEGREES_PER_SIGNAL = 4.0;

    /** The rain gauge holds 15 steps of water (comparator 0–15). */
    public static final int GAUGE_MAX = 15;

    private InstrumentRules() {
    }

    /** Comparator signal (0–15) for a temperature. */
    public static int thermometerSignal(double temperature) {
        long signal = Math.round((temperature - SIGNAL_MIN_TEMPERATURE) / DEGREES_PER_SIGNAL);
        return (int) Math.max(0, Math.min(15, signal));
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
