package net.skross04.wildlifeandseasons.ecology.climate;

/**
 * Foggy mornings and windy days. Each day is decided from the date and the world (the same for every player), with
 * chances that follow the climate zone and the season. Real basis: radiation fog forms on clear, calm, long nights,
 * so it's most common from November to January; it's thickest around sunrise and burns off soon after in autumn but
 * can last all day in midwinter; wind or cloud clears it. European windstorms come from October to March, most in
 * winter (peak around January).
 * <p>
 * Year position: 0 ≈ 9 March, 0.25 ≈ 10 June, 0.5 ≈ 6 September, 0.75 ≈ 7 December. Time of day in ticks:
 * 0 = sunrise, 6000 = noon, 12000 = sunset, 18000 = midnight.
 */
public final class DayWeatherRules {
    /** One day's weather character. */
    public record Day(boolean foggy, boolean windy, boolean allDayFog, boolean frosty) {
        public static final Day CALM = new Day(false, false, false, false);
    }

    /** Fog seen at full strength brings the view down to this many blocks. */
    public static final float FOG_DISTANCE = 32;

    /**
     * In a blizzard the view closes in to this many blocks. Real blizzards: wind of at least 35 mph with falling or
     * blowing snow that cuts the visibility below a quarter mile, for three hours or more.
     */
    public static final float BLIZZARD_DISTANCE = 20;

    /** A ground blizzard (wind whipping up lying snow, none falling) is about half as thick. */
    public static final double GROUND_BLIZZARD = 0.5;

    /** Share of midwinter fog days on which the fog doesn't lift all day. */
    public static final double ALL_DAY_SHARE = 0.35;

    private DayWeatherRules() {
    }

    /** Chance that a day starts foggy. */
    public static double fogChance(ClimateZone zone, double yearPosition) {
        double winter = Math.cos(2 * Math.PI * (yearPosition - 0.80));   // 1 in mid-December, −1 in mid-June
        return switch (zone) {
            case TEMPERATE -> 0.18 + 0.15 * winter;
            case OCEAN -> 0.20 + 0.10 * winter;    // sea fog also in spring and summer
            case COLD -> 0.12 + 0.08 * winter;     // freezing fog
            case TROPICAL -> 0.15;                // morning mist in the rainforest
            case SAVANNA -> 0.04;
            case ARID -> 0.01;
            case NO_SEASONS -> 0;
        };
    }

    /** Chance that a day is windy. */
    public static double windChance(ClimateZone zone, double yearPosition) {
        double storms = Math.cos(2 * Math.PI * (yearPosition - 0.86));   // 1 around mid-January
        return switch (zone) {
            case TEMPERATE -> 0.14 + 0.11 * storms;
            case OCEAN -> 0.18 + 0.12 * storms;
            case COLD -> 0.15 + 0.10 * storms;
            case TROPICAL, SAVANNA -> 0.08;
            case ARID -> 0.12;
            case NO_SEASONS -> 0;
        };
    }

    /** How wintry it is, 0..1 (1 in mid-December): fog lasts longer into the day. */
    private static double winterness(double yearPosition) {
        double w = (1 + Math.cos(2 * Math.PI * (yearPosition - 0.82))) / 2;
        return w * w;
    }

    /** The weather character of one day (wind and fog exclude each other: wind clears fog). */
    public static Day day(ClimateZone zone, double yearPosition, long dayIndex, long seed) {
        boolean windy = hash(dayIndex, seed, 1) < windChance(zone, yearPosition);
        boolean foggy = !windy && hash(dayIndex, seed, 2) < fogChance(zone, yearPosition);
        boolean allDay = foggy && hash(dayIndex, seed, 3) < ALL_DAY_SHARE * winterness(yearPosition);
        boolean frosty = !windy && hash(dayIndex, seed, 4) < frostChance(zone, yearPosition);
        return new Day(foggy, windy, allDay, frosty);
    }

    /**
     * How thick the fog is now, 0..1. The fog of a morning builds up during the night before (from midnight,
     * thickest from 5 a.m. to sunrise) and burns off after sunrise: quickly in autumn, around noon in midwinter.
     *
     * @param today    today's weather
     * @param tomorrow the next day's weather (its fog builds up during tonight)
     */
    public static double fog(Day today, Day tomorrow, double yearPosition, long timeOfDay) {
        long t = Math.floorMod(timeOfDay, 24000L);
        if (t >= 18000) {
            return tomorrow.foggy() ? smoothstep(18000, 23000, t) : 0;
        }
        if (!today.foggy()) {
            return 0;
        }
        if (today.allDayFog()) {
            return t < 1000 ? 1 : 0.75;
        }
        double burnOff = 1500 + 4500 * winterness(yearPosition);   // 7:30 in autumn … noon in midwinter
        return 1 - smoothstep(burnOff * 0.4, burnOff, t);
    }

    /** How hard the wind blows now, 0..1: gusty on windy days, calm otherwise. */
    public static double wind(Day today, long gameTime) {
        if (!today.windy()) {
            return 0;
        }
        double s = gameTime / 20.0;   // seconds
        double gusts = 0.5 * Math.sin(s * 0.21) + 0.3 * Math.sin(s * 0.53 + 1.3) + 0.2 * Math.sin(s * 1.37 + 2.1);
        return Math.max(0.2, Math.min(1, 0.6 + 0.4 * gusts));
    }

    /**
     * Chance of a frosty morning (hoarfrost on grass and leaves). Real basis: hoar frost forms mostly on surfaces near
     * the ground cooled by night radiation under a clear sky and light wind; grass can freeze even when the air
     * stays just above 0 °C, so frosty mornings start in autumn and go on into spring.
     */
    public static double frostChance(ClimateZone zone, double yearPosition) {
        double winter = Math.cos(2 * Math.PI * (yearPosition - 0.86));   // coldest nights around mid-January
        double chance = switch (zone) {
            case TEMPERATE -> 0.12 + 0.42 * winter;
            case COLD -> 0.32 + 0.42 * winter;
            case OCEAN -> 0.03 + 0.28 * winter;
            case TROPICAL, SAVANNA, ARID, NO_SEASONS -> 0;
        };
        return Math.max(0, Math.min(1, chance));
    }

    /**
     * How frosty it is now, 0..1: the frost forms in the second half of the night and melts a few hours after
     * sunrise (around 8–9 a.m. in autumn, towards noon in midwinter).
     */
    public static double hoarfrost(Day today, Day tomorrow, double yearPosition, long timeOfDay) {
        long t = Math.floorMod(timeOfDay, 24000L);
        if (t >= 18000) {
            return tomorrow.frosty() ? smoothstep(19000, 23500, t) : 0;
        }
        if (!today.frosty()) {
            return 0;
        }
        double melt = 2500 + 3500 * winterness(yearPosition);
        return 1 - smoothstep(melt * 0.35, melt, t);
    }

    /** Where the wind blows to today (radians; the same for the whole world, as weather systems are big). */
    public static double windDirection(long dayIndex, long seed) {
        return hash(dayIndex, seed, 7) * 2 * Math.PI;
    }

    /**
     * How strong a blizzard is, 0..1.
     *
     * @param snowing     how hard it snows here (0..1, vanilla's rain level where it snows)
     * @param wind        the wind now (0..1)
     * @param thunder     vanilla's thunder level (a snowstorm with thunder is a blizzard too)
     * @param snowOnGround true if there's snow lying where the player is (for a ground blizzard)
     */
    public static double blizzard(double snowing, double wind, double thunder, boolean snowOnGround) {
        double falling = snowing * Math.max(wind, thunder);
        double ground = snowing < 0.2 && snowOnGround ? GROUND_BLIZZARD * wind : 0;
        return Math.min(1, Math.max(falling, ground));
    }

    /** A random number 0..1 that's always the same for this day, world and salt. */
    static double hash(long day, long seed, int salt) {
        long h = day * 0x9E3779B97F4A7C15L ^ seed ^ (salt + 1L) * 0xC2B2AE3D27D4EB4FL;
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        h *= 0xC4CEB9FE1A85EC53L;
        h ^= h >>> 33;
        return (h >>> 11) * 0x1.0p-53;
    }

    private static double smoothstep(double from, double to, double value) {
        double t = Math.max(0, Math.min(1, (value - from) / (to - from)));
        return t * t * (3 - 2 * t);
    }
}
