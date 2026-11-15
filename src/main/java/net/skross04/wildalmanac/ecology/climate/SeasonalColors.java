package net.skross04.wildalmanac.ecology.climate;

/**
 * Seasonal colours for grass and leaves: fresh green in spring, the normal deep green in summer,
 * autumn colours, and faded colours in winter. Savannas turn yellow in the dry season;
 * tropical, desert and seasonless places keep their colours.
 * <p>
 * Each season "fades in and out" smoothly (a bump curve), so colours change a little every day.
 * Colours are 0xRRGGBB; the alpha of the original colour is kept.
 */
public final class SeasonalColors {
    // Target colours that the normal biome colour is blended towards.
    private static final int SPRING_GRASS = 0x8ED65B, AUTUMN_GRASS = 0xB09A50, WINTER_GRASS = 0x8F9C7C, DRY_GRASS = 0xC9B45E;
    private static final int SPRING_LEAVES = 0x9CDB5C, AUTUMN_LEAVES = 0xD0682A, WINTER_LEAVES = 0x7D8B60, DRY_LEAVES = 0xB5A651;
    private static final int SPRING_BIRCH = 0xA8D86A, AUTUMN_BIRCH = 0xE3C13F, WINTER_BIRCH = 0x9A9C6A;

    private SeasonalColors() {
    }

    /**
     * How strongly each season's look applies right now, each 0–1.
     * {@code dry} is the savanna dry season.
     */
    public record Tint(double spring, double autumn, double winter, double dry) {
        public static final Tint NONE = new Tint(0, 0, 0, 0);

        /** True if this tint differs noticeably from another (used to decide whether to redraw chunks). */
        public boolean differsFrom(Tint other) {
            return Math.abs(spring - other.spring) > 0.005 || Math.abs(autumn - other.autumn) > 0.005
                    || Math.abs(winter - other.winter) > 0.005 || Math.abs(dry - other.dry) > 0.005;
        }
    }

    /** The seasonal look for a zone at a point in the year (0 = start of spring … 1 = end of winter). */
    public static Tint tint(ClimateZone zone, double yearPosition) {
        return switch (zone) {
            case TEMPERATE, OCEAN -> new Tint(
                    bump(yearPosition, 0.10, 0.16),   // around mid-spring
                    bump(yearPosition, 0.64, 0.18),   // late summer → early winter, strongest in mid-autumn
                    bump(yearPosition, 0.88, 0.14),   // around mid-winter
                    0);
            case COLD -> new Tint(
                    bump(yearPosition, 0.20, 0.12),   // spring comes late
                    bump(yearPosition, 0.56, 0.12),   // short, early autumn
                    bump(yearPosition, 0.86, 0.22),   // long winter
                    0);
            case SAVANNA -> new Tint(0, 0, 0, bump(yearPosition, 0.85, 0.28)); // dry season
            case TROPICAL, ARID, NO_SEASONS -> Tint.NONE;
        };
    }

    public static int grass(int base, Tint t) {
        int c = blend(base, SPRING_GRASS, 0.30 * t.spring());
        c = blend(c, AUTUMN_GRASS, 0.45 * t.autumn());
        c = blend(c, WINTER_GRASS, 0.50 * t.winter());
        return blend(c, DRY_GRASS, 0.70 * t.dry());
    }

    public static int foliage(int base, Tint t) {
        int c = blend(base, SPRING_LEAVES, 0.30 * t.spring());
        c = blend(c, AUTUMN_LEAVES, 0.85 * t.autumn());
        c = blend(c, WINTER_LEAVES, 0.40 * t.winter());
        return blend(c, DRY_LEAVES, 0.30 * t.dry());
    }

    /** Birch leaves turn golden yellow in autumn instead of orange. */
    public static int birch(int base, Tint t) {
        int c = blend(base, SPRING_BIRCH, 0.25 * t.spring());
        c = blend(c, AUTUMN_BIRCH, 0.90 * t.autumn());
        c = blend(c, WINTER_BIRCH, 0.35 * t.winter());
        return blend(c, DRY_LEAVES, 0.30 * t.dry());
    }

    /** Mixes two colours: amount 0 = only {@code from}, 1 = only {@code to}. Keeps the alpha of {@code from}. */
    static int blend(int from, int to, double amount) {
        if (amount <= 0) {
            return from;
        }
        double a = Math.min(1.0, amount);
        int r = (int) Math.round(((from >> 16) & 0xFF) * (1 - a) + ((to >> 16) & 0xFF) * a);
        int g = (int) Math.round(((from >> 8) & 0xFF) * (1 - a) + ((to >> 8) & 0xFF) * a);
        int b = (int) Math.round((from & 0xFF) * (1 - a) + (to & 0xFF) * a);
        return (from & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    /**
     * A smooth hill: 1 at {@code center}, falling to 0 at {@code center ± halfWidth}, 0 outside.
     * Wraps around the year, so a winter bump can continue into the next spring.
     */
    static double bump(double yearPosition, double center, double halfWidth) {
        double d = yearPosition - center;
        d -= Math.round(d); // shortest distance around the year, between -0.5 and 0.5
        if (Math.abs(d) >= halfWidth) {
            return 0;
        }
        double c = Math.cos(Math.PI / 2 * d / halfWidth);
        return c * c;
    }
}
