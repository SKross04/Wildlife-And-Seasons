package net.skross04.wildlifeandseasons.ecology.climate;

import net.skross04.wildlifeandseasons.ecology.vegetation.BerryRules;
import net.skross04.wildlifeandseasons.ecology.vegetation.WaterLilyRules;

/**
 * Seasonal colours for grass and leaves, based on how real trees change through the year.
 * <p>
 * Deciduous trees in temperate places go through these stages:
 * <ol>
 *     <li>Early spring: still bare (grey-brown twigs; oaks still hold last year's dry brown leaves).</li>
 *     <li>Leaf-out: light lime green new leaves, which slowly darken.</li>
 *     <li>Summer: deep green, getting a little darker and duller towards late summer.</li>
 *     <li>Autumn: colours by tree type. Most broadleaf trees turn yellow, then red, fewer orange; birch bright
 *     yellow, wild cherry orange to crimson, oaks russet and brown. Then the leaves turn brown and fall.</li>
 *     <li>Winter: bare, or white while snow lies on the branches.</li>
 * </ol>
 * Neighbouring trees (8×8-block patches) turn at slightly different times and get different autumn colours, so a
 * forest looks mixed. Cold-zone forests (taigas) don't turn colour. Savannas yellow in the dry season. In the tropics
 * some trees grow red young leaves when the rains start, and mangroves look a little less green at the end of the
 * dry season. Savanna trees are fresh green in the rainy season. Evergreen azaleas turn purple-bronze in the cold
 * months and flower in mid to late spring, after the cherries.
 * <p>
 * Poplars (Dappled Forest) are green in spring and summer and turn their own red, orange or yellow in autumn.
 * The Dappled Forest's grass is green too, and only takes the biome's own warm colour in autumn.
 * <p>
 * Colours are 0xRRGGBB; the alpha of the original colour is kept.
 */
public final class SeasonalColors {
    // Grass: target colours the normal biome colour is blended towards, and how strongly (at the season's peak).
    private static final int SPRING_GRASS = 0x7FD650, AUTUMN_GRASS = 0xB89A45, WINTER_GRASS = 0x8E9A7E, DRY_GRASS = 0xCDB45A;
    private static final double SPRING_GRASS_AMOUNT = 0.45, AUTUMN_GRASS_AMOUNT = 0.65, WINTER_GRASS_AMOUNT = 0.60, DRY_GRASS_AMOUNT = 0.75;

    /** One kind of deciduous tree: its colours through the year. */
    private record Tree(int fresh, int summer, int[] autumn, int brown, double brownAmount, int bare, double bareAmount,
                        double timing) {
    }

    /** Fresh spring leaves are light and yellowish (thin, with little chlorophyll yet). */
    private static final double FRESH_AMOUNT = 0.60;
    /** Mature leaves get darker and duller during summer. */
    private static final double SUMMER_AMOUNT = 0.30;

    /**
     * Most broadleaf trees (Minecraft's oak stands for a mixed forest). The autumn colours follow how often each
     * colour shows up among temperate trees: about half yellow (incl. yellow-brown), a third red, the rest orange.
     */
    private static final Tree BROADLEAF = new Tree(0xA8D23E, 0x3E6B24, new int[]{
            0xE0B52C, 0xD9AE2A, 0xE6C23A, 0xE0B52C, 0xD9AE2A, 0xE6C23A, // yellow
            0xB4B035,                                                   // green-yellow
            0xB98B34, 0xAE8236,                                         // yellow-brown
            0xDE9426,                                                   // yellow-orange
            0xD8691E, 0xD1601C,                                         // orange
            0xC9521F,                                                   // orange-red
            0xC0381F, 0xB72E22, 0xC73F24, 0xAD2A20,                     // red
            0x9E4528, 0x96402A,                                         // red-brown
            0x8E2C38},                                                  // red-purple
            0x9C6E3E, 0.55, 0x786A5C, 0.80, 0.0);

    /** Dark oak: like real oaks, russet, red-brown and yellow-brown; many keep their dry brown leaves in winter. */
    private static final Tree DARK_OAK = new Tree(0x9CC63E, 0x36601F, new int[]{
            0xA54A26, 0xA54A26, 0x8F3D25, 0x8F3D25, 0xB0843A, 0xB0843A, 0xBF6428, 0xA0301F},
            0x94663A, 0.70, 0x8C6640, 0.85, 0.01);

    /** Birch: very light green leaves, bright yellow in autumn (they fall yellow, browning little); purplish twigs. */
    private static final Tree BIRCH = new Tree(0xB6DE5C, 0x6A9638, new int[]{
            0xEBC53A, 0xEBC53A, 0xEBC53A, 0xE8D14C, 0xE3A82E},
            0xA88A4A, 0.25, 0x7E6A66, 0.80, -0.01);

    /** Wild cherry: bronze young leaves, orange, scarlet and crimson in autumn; reddish-brown twigs. */
    private static final Tree CHERRY = new Tree(0xA89A4A, 0x4C7A30, new int[]{
            0xE5702A, 0xD83A26, 0xC02A3C, 0xA8344A, 0xE2A832},
            0x9A5E3A, 0.35, 0x7A5048, 0.80, 0.0);

    // Cold zones (no autumn colours) and other zones: a simpler look.
    private static final int SPRING_LEAVES = 0xA6D33C, WINTER_LEAVES = 0x7A855C, DRY_LEAVES = 0xB5A651;
    private static final double SPRING_LEAVES_AMOUNT = 0.45, WINTER_LEAVES_AMOUNT = 0.50, DRY_LEAVES_AMOUNT = 0.35;

    /** Red young leaves in the tropics, when the rains begin. */
    private static final int TROPICAL_FLUSH = 0xB8553C;
    private static final double TROPICAL_FLUSH_AMOUNT = 0.60;

    /** Cherry trees: green leaves, pink blossom in spring. */
    private static final int CHERRY_GREEN = 0x6FA84A, CHERRY_PINK = 0xFFB0D6;
    /**
     * Cherry leaves use our own bright texture (so the blossom can be a vivid pink), about 1.5× brighter than
     * vanilla's grey oak leaves. Leaf colours are darkened by this much so cherry trees look like the other trees
     * outside spring.
     */
    private static final double CHERRY_TEXTURE_COMPENSATION = 0.66;
    private static final double CHERRY_BLOSSOM_STRENGTH = 1.6;

    /**
     * Azalea and pale oak leaves have untinted (or too dark) vanilla textures, so they use our own bright grey
     * textures; these colours paint them back to vanilla's look. The snow colours give the same snowy look as on
     * the other trees (whose grey texture is darker).
     */
    private static final int AZALEA_LEAVES = 0x7A9C3C, AZALEA_SNOW = 0xB6BABC;
    /** Evergreen azaleas: leaves turn purple-bronze from late autumn to spring (anthocyanins against the cold). */
    private static final int AZALEA_BRONZE = 0x7A4A3A;
    private static final double AZALEA_BRONZE_AMOUNT = 0.55;
    private static final int AZALEA_FLOWERS = 0xF58AE3;
    /** Azaleas flower from mid spring into early summer (about April to early June), later than cherries. */
    private static final double AZALEA_BLOOM_CENTER = 0.20, AZALEA_BLOOM_HALF_WIDTH = 0.12;
    /** The flower layer's texture is brighter than the leaf layer's: this darkens a leaf colour to match it. */
    private static final double AZALEA_FLOWER_TO_LEAF = 0.84;
    private static final int PALE_OAK_LEAVES = 0x979D94, PALE_OAK_SNOW = 0xAEB1B3;

    /**
     * Poplar leaves use our own bright grey texture (so autumn can be as vivid as Mojang's red, orange and yellow
     * leaves). It's brighter than vanilla's grey oak leaves, so the other colours are darkened by this much.
     */
    private static final double POPLAR_TEXTURE_COMPENSATION = 0.78;
    /** A poplar's own colour shows a little even in summer. */
    private static final double POPLAR_OWN_COLOR_HINT = 0.12;
    private static final int POPLAR_GREEN = darken(0x59AE30, POPLAR_TEXTURE_COMPENSATION);
    private static final int POPLAR_SNOW = darken(0xEEF3F6, POPLAR_TEXTURE_COMPENSATION);

    /** The three poplar leaves. Their autumn colour paints our grey texture like Mojang's leaves look. */
    public enum Poplar {
        RED(0xD13C35), ORANGE(0xFF751F), YELLOW(0xFFB537);

        private final int autumn;
        private final Tree tree;

        Poplar(int autumn) {
            this.autumn = autumn;
            double k = POPLAR_TEXTURE_COMPENSATION;
            // Real poplars and aspens mostly fall while still coloured (little browning).
            this.tree = new Tree(darken(0xA8D23E, k), darken(0x3E6B24, k), new int[]{autumn}, darken(0x9C6E3E, k), 0.35,
                    darken(0x786A5C, k), 0.80, 0.0);
        }

        /** The leaf's own colour: in autumn, in the inventory, and when seasonal poplars are switched off. */
        public int autumnColor() {
            return autumn;
        }
    }

    /** How much of the Dappled Forest's own autumn grass colour shows at the peak of autumn. */
    private static final double AUTUMN_BIOME_GRASS_AMOUNT = 0.90;

    /**
     * Sweet berry bushes (our own grey textures, see the sweet_berry_bush_stage models): green leaves that turn
     * purple-red in autumn and winter (like real lingonberries), and white-pink flowers in early summer. Out of
     * flowering, the flower pixels take exactly the colour of the leaf shade under them, so they can't be seen.
     */
    private static final int BERRY_LEAVES = 0x52A167, BERRY_WINTER_LEAVES = 0x8A404A, BERRY_FLOWERS = 0xFFE4EA;
    /** Grey of the leaf pixels under the flowers ÷ grey of the flower pixels (from the textures: 0xC8 / 0xF4). */
    private static final double BERRY_FLOWER_TO_LEAF = 0xC8 / (double) 0xF4;

    /**
     * Lily pads (our own grey pad texture, see the lily_pad model): Mojang's green in summer, bronze when new in spring,
     * yellow then brown in autumn, dead brown in winter. The flower lies on the pad in three layers (light petals,
     * shaded petals, centre); out of flowering each layer takes exactly the colour of the pad under it.
     */
    private static final int LILY_PAD = 0x208030, LILY_YOUNG = 0x7A4A2E, LILY_YELLOW = 0xA8983A, LILY_DEAD = 0x5C4628;
    private static final int LILY_WHITE = 0xFFFFFF, LILY_PINK = 0xF6A6C4, LILY_CENTRE = 0xF4C030;
    /** Grey of the pad under the flower ÷ grey of each flower layer (from the textures: 0xA0 / 0xF8, 0xD0, 0xE8). */
    public static final double LILY_PETALS_TO_PAD = 0xA0 / (double) 0xF8;
    public static final double LILY_SHADE_TO_PAD = 0xA0 / (double) 0xD0;
    public static final double LILY_CENTRE_TO_PAD = 0xA0 / (double) 0xE8;

    /** Mangroves are evergreen, but the forest is less green at the end of the dry season, when most leaves fall. */
    private static final int MANGROVE_DRY = 0x8A8848;
    private static final double MANGROVE_DRY_AMOUNT = 0.40;

    /** Savanna trees (acacia) leaf out fresh green in the rainy season; the dry season turns them dull yellow. */
    private static final int SAVANNA_WET_LEAVES = 0x7FA83A;
    private static final double SAVANNA_WET_AMOUNT = 0.55;

    /** Snow lying on leaves. */
    private static final int SNOW = 0xEEF3F6;
    private static final double SNOW_ON_LEAVES_AMOUNT = 0.85;
    /** °C below the snow temperature at which leaves are fully snowy. */
    private static final double FULL_SNOW_RANGE = 4.0;

    /** Size of a colour patch in blocks: roughly one tree, so neighbouring trees can differ. */
    private static final int PATCH_SIZE_SHIFT = 3; // 2^3 = 8 blocks
    /** Neighbouring trees turn up to this much of a year earlier or later (2 days in a 100-day year). */
    private static final double TIMING_SPREAD = 0.02;

    private SeasonalColors() {
    }

    /**
     * How strongly each season's look applies right now, each 0–1.
     *
     * @param spring       fresh spring green of grass; also cherry and azalea blossom
     * @param autumn       autumn look of grass
     * @param leafAutumn   how strongly leaves are falling (0 in cold zones: taigas stay green); drives falling
     *                     leaves, leaf litter and wind
     * @param winter       faded winter colours (grass; leaves in cold zones)
     * @param dry          savanna dry season
     * @param wet          savanna rainy season (fresh green trees)
     * @param snow         snow lying on leaves (while the day's average stays below freezing)
     * @param flush        tropical red young leaves
     * @param tropicalDry  end of the tropical dry season (mangroves less green)
     * @param yearPosition the point in the year (deciduous trees: each patch turns at its own time; berry bushes)
     * @param deciduous    true where trees go through the full deciduous year (temperate places)
     * @param zone         the climate zone
     */
    public record Tint(double spring, double autumn, double leafAutumn, double winter, double dry, double wet, double snow,
                       double flush, double tropicalDry, double yearPosition, boolean deciduous, ClimateZone zone) {
        public static final Tint NONE = new Tint(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, ClimateZone.NO_SEASONS);

        /** True if this tint differs noticeably from another (used to decide whether to redraw chunks). */
        public boolean differsFrom(Tint other) {
            return Math.abs(spring - other.spring) > 0.005 || Math.abs(autumn - other.autumn) > 0.005
                    || Math.abs(leafAutumn - other.leafAutumn) > 0.005 || Math.abs(winter - other.winter) > 0.005
                    || Math.abs(dry - other.dry) > 0.005 || Math.abs(wet - other.wet) > 0.005
                    || Math.abs(snow - other.snow) > 0.005
                    || Math.abs(flush - other.flush) > 0.005 || Math.abs(tropicalDry - other.tropicalDry) > 0.005
                    || deciduous != other.deciduous || zone != other.zone
                    || (deciduous || BerryRules.hasBerrySeasons(zone)) && Math.abs(yearPosition - other.yearPosition) > 0.003;
        }
    }

    /** The seasonal look for a zone at a point in the year (0 = start of spring … 1 = end of winter). */
    public static Tint tint(ClimateZone zone, double yearPosition) {
        double snow = snowOnLeaves(zone, yearPosition);
        return switch (zone) {
            case TEMPERATE, OCEAN -> {
                double grassAutumn = bump(yearPosition, 0.64, 0.20); // late summer → early winter
                yield new Tint(
                        bump(yearPosition, 0.10, 0.16),             // around mid-spring
                        grassAutumn,
                        bump(yearPosition, 0.65, 0.10),             // leaves fall from early to late autumn
                        bump(yearPosition, 0.88, 0.14),             // around mid-winter
                        0, 0, snow, 0, 0, yearPosition, true, zone);
            }
            case COLD -> new Tint(
                    bump(yearPosition, 0.20, 0.12),                 // spring comes late
                    0.5 * bump(yearPosition, 0.56, 0.13),           // grass browns a little in a short, early autumn
                    0,                                              // taiga trees stay green
                    bump(yearPosition, 0.86, 0.22),                 // long winter
                    0, 0, snow, 0, 0, yearPosition, false, zone);
            case SAVANNA -> new Tint(0, 0, 0, 0,
                    bump(yearPosition, 0.85, 0.28),                 // dry season
                    bump(yearPosition, 0.35, 0.22),                 // rainy season (rain peaks mid-summer)
                    0, 0, 0, yearPosition, false, zone);
            case TROPICAL -> new Tint(0, 0, 0, 0, 0, 0, 0,
                    bump(yearPosition, 0.10, 0.07),                 // red young leaves when the rains start
                    bump(yearPosition, 0.96, 0.12),                 // end of the dry season
                    yearPosition, false, zone);
            case ARID, NO_SEASONS -> Tint.NONE;
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

    /**
     * Where a deciduous tree is in its year, each 0–1.
     *
     * @param fresh  light green new leaves
     * @param summer darker mature leaves
     * @param color  autumn colour (also last year's leaves still hanging before leaf-out)
     * @param brown  dying brown leaves
     * @param bare   leafless (or only dry leaves left)
     */
    record LeafStage(double fresh, double summer, double color, double brown, double bare) {
    }

    static LeafStage leafStage(double yearPosition) {
        double p = yearPosition - Math.floor(yearPosition);
        double summer = bump(p, 0.42, 0.12);
        if (p < 0.5) {
            // Bare until the buds open (about mid-spring), then light green leaves that darken by early summer.
            double bare = 1 - smoothstep(0.05, 0.15, p);
            double fresh = smoothstep(0.06, 0.16, p) * (1 - smoothstep(0.20, 0.32, p));
            return new LeafStage(fresh, summer, bare, bare, bare);
        }
        // Colours appear from the start of autumn, turn brown after their peak, and the trees are bare by winter.
        return new LeafStage(0, summer, smoothstep(0.52, 0.60, p), smoothstep(0.62, 0.69, p), smoothstep(0.67, 0.75, p));
    }

    public static int grass(int base, Tint t) {
        int c = blend(base, SPRING_GRASS, SPRING_GRASS_AMOUNT * t.spring());
        c = blend(c, AUTUMN_GRASS, AUTUMN_GRASS_AMOUNT * t.autumn());
        c = blend(c, WINTER_GRASS, WINTER_GRASS_AMOUNT * t.winter());
        return blend(c, DRY_GRASS, DRY_GRASS_AMOUNT * t.dry());
    }

    /** Leaves where the position isn't known (modded leaves using the biome colour): one orange autumn colour. */
    public static int foliage(int base, Tint t) {
        return leaves(base, t, BROADLEAF, 10, 0, false);
    }

    /** Most leaves (oak, acacia, jungle, vines) at a position: each patch gets its own autumn colour and timing. */
    public static int foliage(int base, Tint t, int x, int z) {
        return leaves(base, t, BROADLEAF, patchIndex(x, z, BROADLEAF.autumn().length, 0), timingOffset(x, z),
                patchIndex(x, z, 3, 2) == 0);
    }

    /** Dark oak leaves: russet and brown in autumn, dry brown leaves in winter. */
    public static int darkOak(int base, Tint t, int x, int z) {
        return leaves(base, t, DARK_OAK, patchIndex(x, z, DARK_OAK.autumn().length, 0), timingOffset(x, z), false);
    }

    /** Birch leaves: very light green in spring, bright yellow in autumn. */
    public static int birch(int base, Tint t, int x, int z) {
        return leaves(base, t, BIRCH, patchIndex(x, z, BIRCH.autumn().length, 0), timingOffset(x, z), false);
    }

    private static int leaves(int base, Tint t, Tree tree, int autumnColor, double offset, boolean flushes) {
        int c;
        if (t.deciduous()) {
            c = deciduous(base, t, tree, autumnColor, offset);
        } else {
            c = blend(base, SPRING_LEAVES, SPRING_LEAVES_AMOUNT * t.spring());
            c = blend(c, WINTER_LEAVES, WINTER_LEAVES_AMOUNT * t.winter());
            c = blend(c, SAVANNA_WET_LEAVES, SAVANNA_WET_AMOUNT * t.wet());
            c = blend(c, DRY_LEAVES, DRY_LEAVES_AMOUNT * t.dry());
            if (flushes) {
                c = blend(c, TROPICAL_FLUSH, TROPICAL_FLUSH_AMOUNT * t.flush());
            }
        }
        return blend(c, SNOW, SNOW_ON_LEAVES_AMOUNT * t.snow());
    }

    private static int deciduous(int base, Tint t, Tree tree, int autumnColor, double offset) {
        LeafStage stage = leafStage(t.yearPosition() - tree.timing() - offset);
        int c = blend(base, tree.fresh(), FRESH_AMOUNT * stage.fresh());
        c = blend(c, tree.summer(), SUMMER_AMOUNT * stage.summer());
        c = blend(c, tree.autumn()[autumnColor], stage.color());
        c = blend(c, tree.brown(), tree.brownAmount() * stage.brown());
        return blend(c, tree.bare(), tree.bareAmount() * stage.bare());
    }

    /**
     * Cherry leaves: pink blossom in spring (on still bare branches), then bronze young leaves, green in summer,
     * orange to crimson in autumn, bare and snowy in winter.
     * (The leaves use a bright grey texture that this colour paints, see the cherry_leaves model.)
     */
    public static int cherry(Tint t, int x, int z) {
        int leaves = leaves(CHERRY_GREEN, t, CHERRY, patchIndex(x, z, CHERRY.autumn().length, 0), timingOffset(x, z), false);
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

    /**
     * Poplar leaves: green in spring and summer (with a hint of their own colour), their own red, orange or yellow
     * in autumn, then bare and snowy like the other deciduous trees. Each patch of trees turns at its own time.
     */
    public static int poplar(Tint t, Poplar kind, int x, int z) {
        int base = blend(POPLAR_GREEN, kind.autumn, POPLAR_OWN_COLOR_HINT);
        int c;
        if (t.deciduous()) {
            c = deciduous(base, t, kind.tree, 0, timingOffset(x, z));
        } else {
            c = blend(base, darken(SPRING_LEAVES, POPLAR_TEXTURE_COMPENSATION), SPRING_LEAVES_AMOUNT * t.spring());
            c = blend(c, darken(WINTER_LEAVES, POPLAR_TEXTURE_COMPENSATION), WINTER_LEAVES_AMOUNT * t.winter());
            c = blend(c, darken(DRY_LEAVES, POPLAR_TEXTURE_COMPENSATION), DRY_LEAVES_AMOUNT * t.dry());
        }
        return blend(c, POPLAR_SNOW, SNOW_ON_LEAVES_AMOUNT * t.snow());
    }

    /** True while poplar leaves here show their autumn colour (then Mojang's own red/orange/yellow leaf particles fit). */
    public static boolean poplarInAutumnColor(Tint t, int x, int z) {
        if (!t.deciduous()) {
            return false;
        }
        LeafStage stage = leafStage(t.yearPosition() - timingOffset(x, z));
        return stage.color() >= 0.5 && stage.bare() < 0.5;
    }

    /** A poplar leaf colour (made for our bright texture) as a colour for the normal grey falling-leaf particle. */
    public static int poplarParticleColor(int poplarColor) {
        return brighten(poplarColor, 1 / POPLAR_TEXTURE_COMPENSATION);
    }

    /**
     * Grass in a biome whose own grass colour is an autumn colour (the Dappled Forest): normal green in spring and
     * summer, the biome's own colour in autumn, faded in winter.
     *
     * @param green the normal grass green for the biome's temperature and rainfall
     * @param own   the biome's own (autumn) grass colour
     */
    public static int autumnBiomeGrass(int green, int own, Tint t) {
        int c = blend(green, SPRING_GRASS, SPRING_GRASS_AMOUNT * t.spring());
        c = blend(c, own, AUTUMN_BIOME_GRASS_AMOUNT * t.autumn());
        c = blend(c, WINTER_GRASS, WINTER_GRASS_AMOUNT * t.winter());
        return blend(c, DRY_GRASS, DRY_GRASS_AMOUNT * t.dry());
    }

    private static int brighten(int color, double factor) {
        int r = (int) Math.min(255, Math.round(((color >> 16) & 0xFF) * factor));
        int g = (int) Math.min(255, Math.round(((color >> 8) & 0xFF) * factor));
        int b = (int) Math.min(255, Math.round((color & 0xFF) * factor));
        return (color & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    /** Lily pad colour through the year. */
    public static int lilyPad(Tint t) {
        WaterLilyRules.PadStage stage = WaterLilyRules.padStage(t.zone(), t.yearPosition());
        int c = blend(LILY_PAD, LILY_YOUNG, stage.young());
        c = blend(c, LILY_YELLOW, stage.yellow());
        return blend(c, LILY_DEAD, stage.dead());
    }

    /** Mojang's lily pad colour (inventory, or seasonal water lilies off). */
    public static int lilyPadPlain() {
        return LILY_PAD;
    }

    /**
     * One flower layer of the pad at x/z: hidden in the pad colour, or white/pink petals (yellow centre) as far as
     * this pad's flower is open.
     *
     * @param layer 0 = light petals, 1 = shaded petals, 2 = centre
     */
    public static int lilyFlower(int padColor, double flowering, int layer, int x, int z) {
        double toPad = layer == 0 ? LILY_PETALS_TO_PAD : layer == 1 ? LILY_SHADE_TO_PAD : LILY_CENTRE_TO_PAD;
        int hidden = darken(padColor, toPad);
        double open = WaterLilyRules.flowerOpen(flowering, x, z);
        if (open <= 0) {
            return hidden;
        }
        int flower = layer == 2 ? LILY_CENTRE : WaterLilyRules.pink(x, z) ? LILY_PINK : LILY_WHITE;
        return blend(hidden, flower, open);
    }

    /** A flower layer hidden in Mojang's pad colour (inventory). */
    public static int lilyFlowerHidden(int layer) {
        return lilyFlower(LILY_PAD, 0, layer, 0, 0);
    }

    /** Sweet berry bush leaves: green, purple-red from mid-autumn until the new growth in late spring. */
    public static int berryLeaves(Tint t) {
        return blend(BERRY_LEAVES, BERRY_WINTER_LEAVES, BerryRules.winterLeaves(t.zone(), t.yearPosition()));
    }

    /** Sweet berry bush flowers: white-pink while flowering, otherwise hidden in the leaf colour. */
    public static int berryFlowers(Tint t) {
        int hidden = darken(berryLeaves(t), BERRY_FLOWER_TO_LEAF);
        return blend(hidden, BERRY_FLOWERS, Math.min(1.0, 1.6 * BerryRules.flowering(t.zone(), t.yearPosition())));
    }

    /** Berry bush colours for the inventory, no seasons, or seasonal berries switched off. */
    public static int berryLeavesPlain() {
        return BERRY_LEAVES;
    }

    public static int berryFlowersHidden() {
        return darken(BERRY_LEAVES, BERRY_FLOWER_TO_LEAF);
    }

    /** Azalea leaves: evergreen, purple-bronze in the cold months (back to green with the new leaves), snowy in winter. */
    public static int azalea(Tint t) {
        double cold = t.deciduous() ? leafStage(t.yearPosition()).brown() : t.winter();
        int c = blend(AZALEA_LEAVES, AZALEA_BRONZE, AZALEA_BRONZE_AMOUNT * cold);
        return blend(c, AZALEA_SNOW, SNOW_ON_LEAVES_AMOUNT * t.snow());
    }

    /**
     * The flower layer of flowering azalea: pink flowers in spring; the rest of the year the flowers take the colour
     * of the leaves around them, so they disappear into the bush.
     */
    public static int azaleaFlowers(Tint t) {
        int leafColoured = darken(azalea(t), AZALEA_FLOWER_TO_LEAF);
        double bloom = t.deciduous() ? bump(t.yearPosition(), AZALEA_BLOOM_CENTER, AZALEA_BLOOM_HALF_WIDTH) : t.spring();
        return blend(leafColoured, AZALEA_FLOWERS, Math.min(1.0, CHERRY_BLOSSOM_STRENGTH * bloom) * (1 - t.snow()));
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

    /** Mangrove leaves: evergreen; a little less green at the end of the tropical dry season; snowy if it gets that cold. */
    public static int mangrove(int base, Tint t) {
        int c = blend(base, MANGROVE_DRY, MANGROVE_DRY_AMOUNT * t.tropicalDry());
        return blend(c, SNOW, SNOW_ON_LEAVES_AMOUNT * t.snow());
    }

    /** Evergreen needles (spruce): real spruce stays green all year (it keeps its needles for years); snowy in winter. */
    public static int evergreen(int base, Tint t) {
        return blend(base, SNOW, SNOW_ON_LEAVES_AMOUNT * t.snow());
    }

    /** Neighbouring patches of trees turn a little earlier or later (−TIMING_SPREAD … +TIMING_SPREAD). */
    static double timingOffset(int x, int z) {
        return (patchIndex(x, z, 5, 1) - 2) * (TIMING_SPREAD / 2);
    }

    /**
     * Picks one of {@code count} options for the patch a position is in. Always the same answer for the
     * same patch (a hash, not random), so a tree keeps its colour all autumn. Different {@code salt}s give
     * independent choices (colour, timing, …).
     */
    static int patchIndex(int x, int z, int count, int salt) {
        long h = (x >> PATCH_SIZE_SHIFT) * 0x9E3779B97F4A7C15L ^ (z >> PATCH_SIZE_SHIFT) * 0xC2B2AE3D27D4EB4FL
                ^ salt * 0x94D049BB133111EBL;
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

    /** 0 before {@code from}, 1 after {@code to}, a smooth S-curve in between. */
    static double smoothstep(double from, double to, double value) {
        double t = Math.max(0, Math.min(1, (value - from) / (to - from)));
        return t * t * (3 - 2 * t);
    }
}
