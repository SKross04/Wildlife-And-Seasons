package net.skross04.wildalmanac.ecology.climate;

import net.skross04.wildalmanac.ecology.season.Season;

/**
 * The weather schedule. Vanilla picks rain and thunder at random all year; here the season decides:
 * <ul>
 *     <li>Spring: frequent, short showers.</li>
 *     <li>Summer: mostly dry, short rain, and thunderstorms are common.</li>
 *     <li>Autumn: long rainy spells, often.</li>
 *     <li>Winter: long snowfalls (where it's cold enough), thunder is rare.</li>
 * </ul>
 * Plus wet and dry seasons: savannas and jungles only rain in their wet season, deserts only on rare spring days.
 * All durations are in ticks (24000 = one in-game day).
 */
public final class SeasonalWeather {
    private SeasonalWeather() {
    }

    /** A duration between min and max; {@code random} is a random number from 0 to 1. */
    private static int between(int min, int max, double random) {
        return min + (int) Math.round((max - min) * random);
    }

    /** How long a rain (or snow) spell lasts once it starts. */
    public static int rainDuration(Season season, double random) {
        return switch (season) {
            case SPRING -> between(2_000, 6_000, random);
            case SUMMER -> between(3_000, 8_000, random);
            case AUTUMN -> between(12_000, 30_000, random);
            case WINTER -> between(8_000, 20_000, random);
        };
    }

    /** How long it stays dry before the next rain. */
    public static int dryDuration(Season season, double random) {
        return switch (season) {
            case SPRING -> between(6_000, 24_000, random);
            case SUMMER -> between(24_000, 72_000, random);
            case AUTUMN -> between(8_000, 30_000, random);
            case WINTER -> between(12_000, 40_000, random);
        };
    }

    /** How long a thunderstorm lasts (it only shows while it's also raining). */
    public static int thunderDuration(Season season, double random) {
        return season == Season.SUMMER ? between(3_600, 12_000, random) : between(3_600, 8_000, random);
    }

    /** How long until the next thunderstorm. */
    public static int calmDuration(Season season, double random) {
        return switch (season) {
            case SPRING -> between(30_000, 90_000, random);
            case SUMMER -> between(6_000, 30_000, random);
            case AUTUMN -> between(40_000, 120_000, random);
            case WINTER -> between(120_000, 240_000, random);
        };
    }

    /**
     * Wet and dry seasons. Returns whether rain can fall in this zone now, or null where vanilla decides
     * (temperate, cold, ocean and seasonless places keep their normal rain or snow).
     *
     * @param rainfall the climate's rainfall right now (0–1)
     */
    public static Boolean hasRain(ClimateZone zone, double rainfall) {
        return switch (zone) {
            case SAVANNA, TROPICAL -> rainfall >= 0.45;  // wet season around spring and summer, dry season around winter
            case ARID -> rainfall >= 0.10;               // a few rainy days in spring
            case TEMPERATE, COLD, OCEAN, NO_SEASONS -> null;
        };
    }

    /**
     * Seasonal day length: the share of the day that's light (0.4 in winter to 0.6 in summer).
     * The day keeps its 20 minutes; the clock runs slower during the day and faster at night in summer
     * (and the other way round in winter).
     *
     * @return how fast the day clock should run now (1 = normal)
     */
    public static float clockRate(double daylightShare, boolean isDaytime) {
        return (float) (isDaytime ? 1.0 / (2 * daylightShare) : 1.0 / (2 * (1 - daylightShare)));
    }
}
