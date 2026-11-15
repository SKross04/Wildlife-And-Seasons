package net.skross04.wildlifeandseasons.ecology.climate;

/**
 * When it snows instead of rains, when water freezes and when snow and ice thaw.
 * <ul>
 *     <li>Rain or snow and freezing use the temperature right now: at or below {@link #SNOW_TEMPERATURE}
 *     it snows and still water can freeze (also on a single cold night).</li>
 *     <li>Thawing uses the average temperature of the whole day, and only starts above {@link #THAW_TEMPERATURE}.
 *     The gap between the two stops ponds from freezing every night and melting every day.</li>
 * </ul>
 */
public final class SnowAndIce {
    /** °C: at or below this it snows instead of rains, and still water open to the sky can freeze. */
    public static final double SNOW_TEMPERATURE = 0.5;
    /** °C (daily average): above this, snow and ice open to the sky start melting. */
    public static final double THAW_TEMPERATURE = 1.0;
    /** °C above the thaw temperature at which melting reaches full speed. */
    public static final double FULL_THAW_RANGE = 6.0;

    private SnowAndIce() {
    }

    public static boolean isRainTemperature(double temperature) {
        return temperature > SNOW_TEMPERATURE;
    }

    /**
     * Chance (0–1) that a snow layer or ice block melts when Minecraft randomly ticks it.
     * Just above freezing it melts slowly; on warm days quickly.
     */
    public static double meltChance(double dailyMeanTemperature) {
        if (dailyMeanTemperature <= THAW_TEMPERATURE) {
            return 0;
        }
        return Math.min(1.0, (dailyMeanTemperature - THAW_TEMPERATURE) / FULL_THAW_RANGE);
    }
}
