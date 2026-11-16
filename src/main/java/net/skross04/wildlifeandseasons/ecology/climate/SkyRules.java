package net.skross04.wildlifeandseasons.ecology.climate;

/**
 * Northern lights and shooting stars. Real basis:
 * <ul>
 *   <li>Aurora: mostly seen near the poles, on dark, clear nights; geomagnetic storms push it further south. The
 *       common pale green (557.7 nm) is oxygen about 100–150 km up; red oxygen glow sits higher; strong displays get
 *       a red-violet lower border. It's more common around the equinoxes; summer nights up north are too light.</li>
 *   <li>Meteors: about half a dozen random ones an hour on any dark night, more after midnight; showers on fixed
 *       dates: Perseids (peak 12–13 August, 60–80 an hour), Geminids (14 December, ~100), Quadrantids (3–4 January,
 *       short and sharp).</li>
 * </ul>
 * Year position: 0 ≈ 9 March … 1. Day position: 0 = sunrise, 0.5 = sunset, 0.75 = midnight.
 */
public final class SkyRules {
    private SkyRules() {
    }

    /** True at night (between dusk and dawn). */
    public static double night(double dayPosition) {
        return smoothstep(0.54, 0.60, dayPosition) * (1 - smoothstep(0.93, 0.98, dayPosition));
    }

    /** Chance of northern lights on a night. */
    public static double auroraChance(ClimateZone zone, double yearPosition) {
        double p = yearPosition - Math.floor(yearPosition);
        // summer nights near the pole are too light: none from late April to mid-August
        double dark = 1 - smoothstep(0.10, 0.18, p) * (1 - smoothstep(0.42, 0.50, p));
        double equinox = 1 + 0.3 * Math.cos(4 * Math.PI * (p - 0.03));   // peaks around 20 March and 22 September
        double base = switch (zone) {
            case COLD -> 0.40;
            case TEMPERATE -> 0.06;   // only in big storms
            case OCEAN -> 0.05;
            case TROPICAL, SAVANNA, ARID, NO_SEASONS -> 0;
        };
        return Math.min(1, base * dark * equinox);
    }

    /** Strength of tonight's northern lights, 0 (none) to 1 (a big storm). */
    public static double auroraNight(ClimateZone zone, double yearPosition, long dayIndex, long seed) {
        if (DayWeatherRules.hash(dayIndex, seed, 5) >= auroraChance(zone, yearPosition)) {
            return 0;
        }
        double strength = 0.4 + 0.6 * DayWeatherRules.hash(dayIndex, seed, 6);
        // further south only the strong ones are seen, and lower in the sky
        return zone == ClimateZone.COLD ? strength : strength * 0.6;
    }

    /** How bright the northern lights are now, 0..1: through the night, brightest around midnight, slowly pulsing. */
    public static double auroraNow(double nightStrength, double dayPosition, long gameTime) {
        if (nightStrength <= 0) {
            return 0;
        }
        double midnight = Math.exp(-Math.pow((dayPosition - 0.76) / 0.12, 2));
        double pulse = 0.75 + 0.25 * Math.sin(gameTime / 900.0) * Math.sin(gameTime / 370.0 + 1.1);
        return nightStrength * night(dayPosition) * (0.4 + 0.6 * midnight) * pulse;
    }

    /** Expected shooting stars per tick at night (random ones plus the big showers). */
    public static double meteorsPerTick(double yearPosition, double dayPosition) {
        double p = yearPosition - Math.floor(yearPosition);
        double afterMidnight = dayPosition > 0.75 ? 1.5 : 1.0;
        double showers = 10 * peak(p, 0.438, 0.012)    // Perseids, 12 August
                + 12 * peak(p, 0.778, 0.008)           // Geminids, 14 December
                + 8 * peak(p, 0.832, 0.004);           // Quadrantids, 3 January
        return night(dayPosition) * afterMidnight * (1 + showers) / 1800.0;   // ~1 per 90 s of night normally
    }

    private static double peak(double p, double center, double width) {
        double d = Math.abs(p - center);
        d = Math.min(d, 1 - d);
        return Math.exp(-0.5 * Math.pow(d / width, 2));
    }

    private static double smoothstep(double from, double to, double value) {
        double t = Math.max(0, Math.min(1, (value - from) / (to - from)));
        return t * t * (3 - 2 * t);
    }
}
