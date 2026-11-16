package net.skross04.wildlifeandseasons.ecology.climate;

/**
 * Seasonal colours for grass and leaves: fresh green in spring, the normal deep green in summer,
 * autumn colours, faded colours in winter, and white snowy leaves while snow lies. Savannas turn yellow
 * in the dry season; tropical, desert and seasonless places keep their colours.
 * <p>
 * Each season "fades in and out" smoothly (a bump curve), so colours change a little every day.
 * In autumn, leaves get patches of red, orange and yellow depending on where they are,
 * so a forest looks mixed instead of one flat colour. Cold-zone forests (taigas) don't turn orange.
 * Colours are 0xRRGGBB; the alpha of the original colour is kept.
 */
public final class SeasonalColors {
    // Target colours that the normal biome colour is blended towards, and how strongly (at the season's peak).
    private static final int SPRING_GRASS = 0x7FD650, AUTUMN_GRASS = 0xB89A45, WINTER_GRASS = 0x8E9A7E, DRY_GRASS = 0xCDB45A;
    private static final double SPRING_GRASS_AMOUNT = 0.45, AUTUMN_GRASS_AMOUNT = 0.65, WINTER_GRASS_AMOUNT = 0.60, DRY_GRASS_AMOUNT = 0.75;

    private static final int SPRING_LEAVES = 0x92DB4F, WINTER_LEAVES = 0x7A855C, DRY_LEAVES = 0xB5A651;
    /** Autumn leaf colours: red, orange, yellow. */
    private static final int[] AUTUMN_LEAVES = {0xC0381F, 0xD86A1E, 0xD9A62B};
    private static final double SPRING_LEAVES_AMOUNT = 0.45, AUTUMN_LEAVES_AMOUNT = 1.0, WINTER_LEAVES_AMOUNT = 0.50, DRY_LEAVES_AMOUNT = 0.35;

    private static final int SPRING_BIRCH = 0xA8DE62, WINTER_BIRCH = 0x9A9C6A;
    /** Birch turns golden; some trees more amber. */
    private static final int[] AUTUMN_BIRCH = {0xE8C23A, 0xE09A2C};
    private static final double SPRING_BIRCH_AMOUNT = 0.40, AUTUMN_BIRCH_AMOUNT = 1.0, WINTER_BIRCH_AMOUNT = 0.45;

    /** Cherry trees: pink blossom in spring, green in summer. */
    private static final int CHERRY_GREEN = 0x6FA84A, CHERRY_PINK = 0xFFB0D6;
    /**
     * Cherry leaves use our own bright texture (so the blossom can be a vivid pink), about 1.5× brighter than
     * vanilla's grey oak leaves. Green, autumn and snow colours are darkened by this much so cherry trees look
     * like the other trees outside spring.
     */
    private static final double CHERRY_TEXTURE_COMPENSATION = 0.66;
    private static final double CHERRY_BLOSSOM_STRENGTH = 1.6;

    /**
     * Azalea and pale oak leaves have untinted (or too dark) vanilla textures, so they use our own bright grey
     * textures; these colours paint them back to vanilla's look. The snow colours give the same snowy look as on
     * the other trees (whose grey texture is darker).
     */
    private static final int AZALEA_LEAVES = 0x7A9C3C, AZALEA_SNOW = 0xB6BABC;
    private static final int AZALEA_FLOWERS = 0xF58AE3;
    /** The flower layer's texture is brighter than the leaf layer's: this darkens a leaf colour to match it. */
    private static final double AZALEA_FLOWER_TO_LEAF = 0.84;
    private static final int PALE_OAK_LEAVES = 0x979D94, PALE_OAK_SNOW = 0xAEB1B3;

    /** Snow lying on leaves. */
    private static final int SNOW = 0xEEF3F6;
    private static final double SNOW_ON_LEAVES_AMOUNT = 0.85;
    /** °C below the snow temperature at which leaves are fully snowy. */
    private static final double FULL_SNOW_RANGE = 4.0;

    /** Size of a colour patch in blocks: roughly one tree, so neighbouring trees can differ. */
    private static final int PATCH_SIZE_SHIFT = 3; // 2^3 = 8 blocks

    private SeasonalColors() {
    }

    /**
     * How strongly each season's look applies right now, each 0–1.
     *
     * @param spring     fresh spring green
     * @param autumn     autumn look of grass
     * @param leafAutumn autumn colours of leaves (0 in cold zones: taigas stay green); also drives falling leaves and leaf litter
     * @param winter     faded winter colours
     * @param dry        savanna dry season
     * @param snow       snow lying on leaves (while the day's average stays below freezing)
     */
    public record Tint(double spring, double autumn, double leafAutumn, double winter, double dry, double snow) {
        public static final Tint NONE = new Tint(0, 0, 0, 0, 0, 0);

        /** True if this tint differs noticeably from another (used to decide whether to redraw chunks). */
        public boolean differsFrom(Tint other) {
            return Math.abs(spring - other.spring) > 0.005 || Math.abs(autumn - other.autumn) > 0.005
                    || Math.abs(leafAutumn - other.leafAutumn) > 0.005 || Math.abs(winter - other.winter) > 0.005
                    || Math.abs(dry - other.dry) > 0.005 || Math.abs(snow - other.snow) > 0.005;
        }
    }

    /** The seasonal look for a zone at a point in the year (0 = start of spring … 1 = end of winter). */
    public static Tint tint(ClimateZone zone, double yearPosition) {
        double snow = snowOnLeaves(zone, yearPosition);
        return switch (zone) {
            case TEMPERATE, OCEAN -> {
                double autumn = bump(yearPosition, 0.64, 0.20); // late summer → early winter, strongest in mid-autumn
                yield new Tint(
                        bump(yearPosition, 0.10, 0.16),         // around mid-spring
                        autumn, autumn,
                        bump(yearPosition, 0.88, 0.14),         // around mid-winter
                        0, snow);
            }
            case COLD -> new Tint(
                    bump(yearPosition, 0.20, 0.12),             // spring comes late
                    0.5 * bump(yearPosition, 0.56, 0.13),       // grass browns a little in a short, early autumn
                    0,                                          // taiga trees stay green
                    bump(yearPosition, 0.86, 0.22),             // long winter
                    0, snow);
            case SAVANNA -> new Tint(0, 0, 0, 0, bump(yearPosition, 0.85, 0.28), 0); // dry season
            case TROPICAL, ARID, NO_SEASONS -> Tint.NONE;
        };
    }

    /**
     * How snowy leaves look: 0 while the day's average temperature is above the snow temperature,
     * rising to 1 when it's {@link #FULL_SNOW_RANGE} °C colder. Uses the zone's typical biome at ground level.
     */
    static double snowOnLeaves(ClimateZone zone, double yearPosition) {
        if (!zone.hasSeasons()) {
            return 0;
        }
        double mean = ClimateModel.dailyMeanTemperature(zone, zone.referenceBiomeTemperature, 0, 64, yearPosition);
        return Math.max(0, Math.min(1, (SnowAndIce.SNOW_TEMPERATURE - mean) / FULL_SNOW_RANGE));
    }

    public static int grass(int base, Tint t) {
        int c = blend(base, SPRING_GRASS, SPRING_GRASS_AMOUNT * t.spring());
        c = blend(c, AUTUMN_GRASS, AUTUMN_GRASS_AMOUNT * t.autumn());
        c = blend(c, WINTER_GRASS, WINTER_GRASS_AMOUNT * t.winter());
        return blend(c, DRY_GRASS, DRY_GRASS_AMOUNT * t.dry());
    }

    /** Leaves with one autumn colour (orange), for leaves where the position isn't known. */
    public static int foliage(int base, Tint t) {
        return foliage(base, t, 1);
    }

    /** Leaves at a position: the autumn colour (red, orange or yellow) depends on the patch they're in. */
    public static int foliage(int base, Tint t, int x, int z) {
        return foliage(base, t, patchColor(x, z, AUTUMN_LEAVES.length));
    }

    private static int foliage(int base, Tint t, int autumnColor) {
        int c = blend(base, SPRING_LEAVES, SPRING_LEAVES_AMOUNT * t.spring());
        c = blend(c, AUTUMN_LEAVES[autumnColor], AUTUMN_LEAVES_AMOUNT * t.leafAutumn());
        c = blend(c, WINTER_LEAVES, WINTER_LEAVES_AMOUNT * t.winter());
        c = blend(c, DRY_LEAVES, DRY_LEAVES_AMOUNT * t.dry());
        return blend(c, SNOW, SNOW_ON_LEAVES_AMOUNT * t.snow());
    }

    /** Birch leaves turn golden (some amber) in autumn. */
    public static int birch(int base, Tint t, int x, int z) {
        int c = blend(base, SPRING_BIRCH, SPRING_BIRCH_AMOUNT * t.spring());
        c = blend(c, AUTUMN_BIRCH[patchColor(x, z, AUTUMN_BIRCH.length)], AUTUMN_BIRCH_AMOUNT * t.leafAutumn());
        c = blend(c, WINTER_BIRCH, WINTER_BIRCH_AMOUNT * t.winter());
        c = blend(c, DRY_LEAVES, DRY_LEAVES_AMOUNT * t.dry());
        return blend(c, SNOW, SNOW_ON_LEAVES_AMOUNT * t.snow());
    }

    /**
     * Cherry leaves: pink blossom in spring, green in summer, autumn colours, snowy in winter.
     * (The leaves use a bright grey texture that this colour paints, see the cherry_leaves model.)
     */
    public static int cherry(Tint t, int x, int z) {
        int leaves = blend(CHERRY_GREEN, AUTUMN_LEAVES[patchColor(x, z, AUTUMN_LEAVES.length)], AUTUMN_LEAVES_AMOUNT * t.leafAutumn());
        leaves = blend(leaves, WINTER_LEAVES, WINTER_LEAVES_AMOUNT * t.winter());
        leaves = blend(leaves, SNOW, SNOW_ON_LEAVES_AMOUNT * t.snow());
        leaves = darken(leaves, CHERRY_TEXTURE_COMPENSATION);
        return blend(leaves, CHERRY_PINK, Math.min(1.0, CHERRY_BLOSSOM_STRENGTH * t.spring()));
    }

    private static int darken(int color, double factor) {
        int r = (int) Math.round(((color >> 16) & 0xFF) * factor);
        int g = (int) Math.round(((color >> 8) & 0xFF) * factor);
        int b = (int) Math.round((color & 0xFF) * factor);
        return (color & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    /** Cherry pink, for the inventory and when seasonal colours are switched off. */
    public static int cherryBlossom() {
        return CHERRY_PINK;
    }

    /** Azalea leaves: evergreen, snowy in winter. */
    public static int azalea(Tint t) {
        return blend(AZALEA_LEAVES, AZALEA_SNOW, SNOW_ON_LEAVES_AMOUNT * t.snow());
    }

    /**
     * The flower layer of flowering azalea: pink flowers in spring; the rest of the year the flowers take the colour
     * of the leaves around them, so they disappear into the bush.
     */
    public static int azaleaFlowers(Tint t) {
        int leafColoured = darken(azalea(t), AZALEA_FLOWER_TO_LEAF);
        return blend(leafColoured, AZALEA_FLOWERS, Math.min(1.0, CHERRY_BLOSSOM_STRENGTH * t.spring()) * (1 - t.snow()));
    }

    /** Pale oak leaves: always pale, snowy in winter. */
    public static int paleOak(Tint t) {
        return blend(PALE_OAK_LEAVES, PALE_OAK_SNOW, SNOW_ON_LEAVES_AMOUNT * t.snow());
    }

    /** Colours for the inventory and for places without seasons. */
    public static int azaleaLeaves() {
        return AZALEA_LEAVES;
    }

    public static int azaleaFlowerColor() {
        return AZALEA_FLOWERS;
    }

    public static int paleOakLeaves() {
        return PALE_OAK_LEAVES;
    }

    /** Evergreen needles (spruce): stay green all year, but get snowy in winter. */
    public static int evergreen(int base, Tint t) {
        return blend(base, SNOW, SNOW_ON_LEAVES_AMOUNT * t.snow());
    }

    /**
     * Picks one of {@code count} colours for the patch a position is in. Always the same answer for the
     * same patch (a hash, not random), so a tree keeps its colour all autumn.
     */
    static int patchColor(int x, int z, int count) {
        long h = (x >> PATCH_SIZE_SHIFT) * 0x9E3779B97F4A7C15L ^ (z >> PATCH_SIZE_SHIFT) * 0xC2B2AE3D27D4EB4FL;
        h ^= h >>> 31;
        h *= 0xBF58476D1CE4E5B9L;
        h ^= h >>> 29;
        return (int) Math.floorMod(h, (long) count);
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
