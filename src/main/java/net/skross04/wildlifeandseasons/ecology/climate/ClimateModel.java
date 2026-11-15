package net.skross04.wildlifeandseasons.ecology.climate;

/**
 * Works out the climate from the zone, the time of year and the time of day.
 * <p>
 * Everything follows smooth curves (cosine waves), so the climate changes a little every day
 * instead of jumping when a new season starts.
 * <p>
 * Year position: 0 = first day of spring, 0.25 = summer, 0.5 = autumn, 0.75 = winter.
 * Mid-summer (0.375) is the warmest and longest day, mid-winter (0.875) the coldest and shortest.
 */
public final class ClimateModel {
    /** Year position of the warmest, longest day: the middle of summer. */
    public static final double MID_SUMMER = 0.375;
    /** Time of day of the warmest moment: 14:00 (the day starts at 06:00 in Minecraft, so 8 of 24 hours later). */
    public static final double WARMEST_TIME_OF_DAY = 8.0 / 24.0;
    /** Height where it starts getting colder; same as vanilla. */
    public static final int LAPSE_START_Y = 80;
    /** °C colder per block above {@link #LAPSE_START_Y}. */
    public static final double LAPSE_PER_BLOCK = 0.05;
    /** °C per step of vanilla biome temperature away from the zone's reference (e.g. birch forest vs plains). */
    public static final double BIOME_TEMPERATURE_SCALE = 10.0;
    /** The biome adjustment never moves a biome further than this from its zone, °C. */
    public static final double MAX_BIOME_SHIFT = 6.0;

    private ClimateModel() {
    }

    /**
     * @param zone             the biome's climate zone
     * @param biomeTemperature the biome's vanilla temperature value (plains 0.8, taiga 0.25, …)
     * @param extraOffset      extra °C for special cases (e.g. warm or frozen oceans); usually 0
     * @param y                block height
     * @param yearPosition     0–1 through the year (see class description)
     * @param dayPosition      0–1 through the day, 0 = sunrise (06:00), 0.25 = noon, 0.75 = midnight
     */
    public static ClimateState sample(ClimateZone zone, double biomeTemperature, double extraOffset, int y,
                                      double yearPosition, double dayPosition) {
        if (!zone.hasSeasons()) {
            // Nether and End: no seasons, no weather, just the biome's own warmth.
            return new ClimateState(biomeTemperature * 20.0, 0.0, 0.5);
        }

        double yearWave = Math.cos(2 * Math.PI * (yearPosition - MID_SUMMER));    // 1 mid-summer, -1 mid-winter
        double warmth = Math.pow((1 + yearWave) / 2, zone.skew);                   // 0 mid-winter .. 1 mid-summer
        double seasonal = zone.winterTemperature + (zone.summerTemperature - zone.winterTemperature) * warmth;

        double biomeShift = clamp((biomeTemperature - zone.referenceBiomeTemperature) * BIOME_TEMPERATURE_SCALE,
                -MAX_BIOME_SHIFT, MAX_BIOME_SHIFT);
        double dayNight = zone.dayNightSwing * Math.cos(2 * Math.PI * (dayPosition - WARMEST_TIME_OF_DAY));
        double altitude = -Math.max(0, y - LAPSE_START_Y) * LAPSE_PER_BLOCK;
        double temperature = seasonal + biomeShift + extraOffset + dayNight + altitude;

        double rainfall = clamp(zone.rainfallMean
                + zone.rainfallAmplitude * Math.cos(2 * Math.PI * (yearPosition - zone.rainfallPeak)), 0.0, 1.0);
        double daylight = 0.5 + zone.daylightSwing * yearWave;

        return new ClimateState(temperature, rainfall, daylight);
    }

    /** Like {@link #sample}, but the average over the whole day (no day/night difference). Used for slow things like thawing. */
    public static double dailyMeanTemperature(ClimateZone zone, double biomeTemperature, double extraOffset, int y, double yearPosition) {
        // A quarter day after the warmest moment, the day/night wave is exactly 0.
        return sample(zone, biomeTemperature, extraOffset, y, yearPosition, WARMEST_TIME_OF_DAY + 0.25).temperature();
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
