package net.skross04.wildlifeandseasons.ecology.vegetation;

import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;

/**
 * When water lilies (Minecraft's lily pads) flower and how their leaves look through the year. Based on the European
 * white water lily (Nymphaea alba): flowers June to September; the floating leaves come up in spring (bronze at
 * first in many water lilies), turn yellow in autumn and die back. In the game the pads stay (you can still walk on
 * them), but they look dead and brown in winter. Warm zones: flowers all year.
 * <p>
 * Year position: 0 ≈ 9 March, 0.25 ≈ 10 June, 0.5 ≈ 6 September (see SeasonalColors).
 */
public final class WaterLilyRules {
    /** Share of pads that carry a flower at the height of summer (most real leaves have no flower next to them). */
    public static final double FLOWERING_SHARE = 0.4;

    /** Share of the flowers that are pink (pink forms of the white water lily occur, e.g. in Swedish lakes). */
    public static final double PINK_SHARE = 0.2;

    private WaterLilyRules() {
    }

    /** Zones with a lily year; elsewhere they flower all year and stay green. */
    public static boolean hasLilySeasons(ClimateZone zone) {
        return zone == ClimateZone.TEMPERATE || zone == ClimateZone.OCEAN || zone == ClimateZone.COLD;
    }

    /** Cold zones start later and end earlier. */
    private static double shift(ClimateZone zone) {
        return zone == ClimateZone.COLD ? 0.03 : 0;
    }

    /** How much the lilies flower now, 0..1: from early June, at their best in July–August, over by late September. */
    public static double flowering(ClimateZone zone, double yearPosition) {
        if (!hasLilySeasons(zone)) {
            return 1;
        }
        double p = wrap(yearPosition), s = shift(zone);
        return smoothstep(0.23 + s, 0.30 + s, p) * (1 - smoothstep(0.52 - s, 0.58 - s, p));
    }

    /**
     * How open the flower of the pad at x/z is, 0..1. Only some pads have a flower; each opens at its own point as
     * the season comes in, so the number of flowers grows in early summer and drops again in September.
     */
    public static double flowerOpen(double flowering, int x, int z) {
        if (hash(x, z, 0) >= FLOWERING_SHARE) {
            return 0;
        }
        double threshold = 0.05 + 0.80 * hash(x, z, 1);
        return smoothstep(threshold, threshold + 0.10, flowering);
    }

    /** True if the flower of the pad at x/z is pink. */
    public static boolean pink(int x, int z) {
        return hash(x, z, 2) < PINK_SHARE;
    }

    /**
     * The leaf stage: {@code young} = new bronze-red leaves in spring, {@code yellow} = autumn yellowing,
     * {@code dead} = brown, dead leaves (late autumn and winter).
     */
    public record PadStage(double young, double yellow, double dead) {
        public static final PadStage SUMMER = new PadStage(0, 0, 0);
    }

    public static PadStage padStage(ClimateZone zone, double yearPosition) {
        if (!hasLilySeasons(zone)) {
            return PadStage.SUMMER;
        }
        double p = wrap(yearPosition), s = shift(zone);
        if (p < 0.4) {
            // New leaves from mid-April (bronze at first), green by late May.
            double grown = smoothstep(0.08 + s, 0.14 + s, p);
            return new PadStage(grown * (1 - smoothstep(0.17 + s, 0.25 + s, p)), 0, 1 - grown);
        }
        // Yellow from late September, brown and dead by early November.
        return new PadStage(0, smoothstep(0.55 - s, 0.62 - s, p), smoothstep(0.63 - s, 0.71 - s, p));
    }

    private static double wrap(double p) {
        return p - Math.floor(p);
    }

    /** A random number 0..1 that's always the same for this block (and salt). */
    static double hash(int x, int z, int salt) {
        long h = x * 0x9E3779B97F4A7C15L ^ z * 0xC2B2AE3D27D4EB4FL ^ (salt + 1) * 0x165667B19E3779F9L;
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
