package net.skross04.wildlifeandseasons.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.skross04.wildlifeandseasons.ClientConfig;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.climate.BiomeZones;
import net.skross04.wildlifeandseasons.climate.ClimateSnapshot;
import net.skross04.wildlifeandseasons.ecology.climate.SeasonalColors;
import net.skross04.wildlifeandseasons.season.SeasonAccess;

/**
 * Keeps the seasonal grass and leaf colours (and the client's climate snapshot) up to date on the player's screen.
 * Grass and most leaves are handled by {@code BiomeColorMixin}; birch leaves get their own colour here,
 * because vanilla gives them a fixed colour instead of the biome's.
 */
@EventBusSubscriber(modid = WildlifeAndSeasons.MODID, value = Dist.CLIENT)
public class SeasonalColorEvents {
    static {
        // Lets BiomeZones find the client world's biome tags (used when playing on someone else's server).
        BiomeZones.setClientRegistries(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            return level == null ? null : level.registryAccess();
        });
    }

    /** Vanilla leaves (oak, acacia, jungle, vines): seasonal, with yellow/orange/red patches in autumn. */
    public static final ColorResolver LEAVES_RESOLVER =
            (biome, x, z) -> SeasonalColorState.leaves(biome, Mth.floor(x), Mth.floor(z));

    /** Dark oak leaves: russet and brown in autumn, like real oaks. */
    public static final ColorResolver DARK_OAK_RESOLVER =
            (biome, x, z) -> SeasonalColorState.darkOak(biome, Mth.floor(x), Mth.floor(z));

    /** Birch leaves start from birch green and turn bright yellow in autumn. */
    public static final ColorResolver BIRCH_RESOLVER =
            (biome, x, z) -> SeasonalColorState.birch(biome, FoliageColor.FOLIAGE_BIRCH, Mth.floor(x), Mth.floor(z));

    /** Spruce needles: evergreen, but snowy in winter. */
    public static final ColorResolver EVERGREEN_RESOLVER =
            (biome, x, z) -> SeasonalColorState.evergreen(biome, FoliageColor.FOLIAGE_EVERGREEN);

    /** Cherry leaves: pink in spring, green in summer, autumn colours, snowy in winter. */
    public static final ColorResolver CHERRY_RESOLVER =
            (biome, x, z) -> SeasonalColorState.cherry(biome, Mth.floor(x), Mth.floor(z));

    /** Mangrove leaves: evergreen (tropical), a little less green late in the dry season, snowy if it ever gets that cold. */
    public static final ColorResolver MANGROVE_RESOLVER =
            (biome, x, z) -> SeasonalColorState.mangrove(biome, FoliageColor.FOLIAGE_MANGROVE);

    /** Azalea leaves (own grey texture): evergreen, purple-bronze in the cold months, snowy in winter. */
    public static final ColorResolver AZALEA_RESOLVER = (biome, x, z) -> SeasonalColorState.azalea(biome);

    /** The flowers of flowering azalea (second layer): pink in spring, leaf-coloured the rest of the year. */
    public static final ColorResolver AZALEA_FLOWER_RESOLVER = (biome, x, z) -> SeasonalColorState.azaleaFlowers(biome);

    /** Pale oak leaves (own grey texture): pale all year, snowy in winter. */
    public static final ColorResolver PALE_OAK_RESOLVER = (biome, x, z) -> SeasonalColorState.paleOak(biome);

    /** Poplar leaves (own grey texture): green until autumn, then their own red, orange or yellow; snowy in winter. */
    public static final ColorResolver RED_POPLAR_RESOLVER =
            (biome, x, z) -> SeasonalColorState.poplar(biome, SeasonalColors.Poplar.RED, Mth.floor(x), Mth.floor(z));
    public static final ColorResolver ORANGE_POPLAR_RESOLVER =
            (biome, x, z) -> SeasonalColorState.poplar(biome, SeasonalColors.Poplar.ORANGE, Mth.floor(x), Mth.floor(z));
    public static final ColorResolver YELLOW_POPLAR_RESOLVER =
            (biome, x, z) -> SeasonalColorState.poplar(biome, SeasonalColors.Poplar.YELLOW, Mth.floor(x), Mth.floor(z));

    /** Sweet berry bushes (own grey textures): leaf colour and flower colour through the year. */
    public static final ColorResolver BERRY_LEAVES_RESOLVER = (biome, x, z) -> SeasonalColorState.berryLeaves(biome);
    public static final ColorResolver BERRY_FLOWERS_RESOLVER = (biome, x, z) -> SeasonalColorState.berryFlowers(biome);

    /** Lily pads (own grey texture): leaf colour through the year. */
    public static final ColorResolver LILY_PAD_RESOLVER = (biome, x, z) -> SeasonalColorState.lilyPad(biome);

    /** How much water lilies flower (0..255 in the lowest byte, not a real colour). */
    public static final ColorResolver LILY_BLOOM_RESOLVER = (biome, x, z) -> SeasonalColorState.lilyBloom(biome);

    /** Custom colour resolvers must be registered, so Minecraft gives them a colour cache. */
    @SubscribeEvent
    public static void registerColorResolvers(RegisterColorHandlersEvent.ColorResolvers event) {
        event.register(LEAVES_RESOLVER);
        event.register(DARK_OAK_RESOLVER);
        event.register(BIRCH_RESOLVER);
        event.register(EVERGREEN_RESOLVER);
        event.register(CHERRY_RESOLVER);
        event.register(MANGROVE_RESOLVER);
        event.register(AZALEA_RESOLVER);
        event.register(AZALEA_FLOWER_RESOLVER);
        event.register(PALE_OAK_RESOLVER);
        event.register(RED_POPLAR_RESOLVER);
        event.register(ORANGE_POPLAR_RESOLVER);
        event.register(YELLOW_POPLAR_RESOLVER);
        event.register(BERRY_LEAVES_RESOLVER);
        event.register(BERRY_FLOWERS_RESOLVER);
        event.register(LILY_PAD_RESOLVER);
        event.register(LILY_BLOOM_RESOLVER);
    }

    /**
     * Give vanilla leaves our colours. This event runs after vanilla registered its own colours, so ours replace them.
     * Falling-leaf particles take their colour from here too.
     */
    @SubscribeEvent
    public static void registerBlockTints(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(seasonalTint(FoliageColor.FOLIAGE_DEFAULT, LEAVES_RESOLVER)),
                Blocks.OAK_LEAVES, Blocks.ACACIA_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.VINE);
        event.register(List.of(seasonalTint(FoliageColor.FOLIAGE_DEFAULT, DARK_OAK_RESOLVER)), Blocks.DARK_OAK_LEAVES);
        event.register(List.of(seasonalTint(FoliageColor.FOLIAGE_BIRCH, BIRCH_RESOLVER)), Blocks.BIRCH_LEAVES);
        event.register(List.of(seasonalTint(FoliageColor.FOLIAGE_EVERGREEN, EVERGREEN_RESOLVER)), Blocks.SPRUCE_LEAVES);
        // Cherry leaves get our own bright grey texture (models/block/cherry_leaves.json override), so this colour
        // decides pink or green.
        event.register(List.of(seasonalTint(SeasonalColors.cherryBlossom(), CHERRY_RESOLVER)), Blocks.CHERRY_LEAVES);
        event.register(List.of(seasonalTint(FoliageColor.FOLIAGE_MANGROVE, MANGROVE_RESOLVER)), Blocks.MANGROVE_LEAVES);
        // Azalea and pale oak also get our own grey textures (assets/minecraft model overrides).
        event.register(List.of(seasonalTint(SeasonalColors.azaleaLeaves(), AZALEA_RESOLVER)), Blocks.AZALEA_LEAVES);
        // Flowering azalea has two layers: tint 0 = leaves, tint 1 = flowers.
        event.register(List.of(seasonalTint(SeasonalColors.azaleaLeaves(), AZALEA_RESOLVER),
                seasonalTint(SeasonalColors.azaleaFlowerColor(), AZALEA_FLOWER_RESOLVER)), Blocks.FLOWERING_AZALEA_LEAVES);
        event.register(List.of(seasonalTint(SeasonalColors.paleOakLeaves(), PALE_OAK_RESOLVER)), Blocks.PALE_OAK_LEAVES);
        // Poplar leaves (Dappled Forest) get our own grey texture too; in the inventory they keep Mojang's colour.
        event.register(List.of(seasonalTint(SeasonalColors.Poplar.RED.autumnColor(), RED_POPLAR_RESOLVER)), Blocks.RED_POPLAR_LEAVES);
        event.register(List.of(seasonalTint(SeasonalColors.Poplar.ORANGE.autumnColor(), ORANGE_POPLAR_RESOLVER)),
                Blocks.ORANGE_POPLAR_LEAVES);
        event.register(List.of(seasonalTint(SeasonalColors.Poplar.YELLOW.autumnColor(), YELLOW_POPLAR_RESOLVER)),
                Blocks.YELLOW_POPLAR_LEAVES);
        // Sweet berry bushes (stages 1–3 use our grey leaves): tint 0 = leaves, tint 1 = flowers (stage 1 only).
        event.register(List.of(seasonalTint(SeasonalColors.berryLeavesPlain(), BERRY_LEAVES_RESOLVER),
                seasonalTint(SeasonalColors.berryFlowersHidden(), BERRY_FLOWERS_RESOLVER)), Blocks.SWEET_BERRY_BUSH);
        // Lily pads (our model): tint 0 = pad, 1 = light petals, 2 = shaded petals, 3 = flower centre.
        event.register(List.of(seasonalTint(SeasonalColors.lilyPadPlain(), LILY_PAD_RESOLVER),
                lilyFlowerTint(0), lilyFlowerTint(1), lilyFlowerTint(2)), Blocks.LILY_PAD);
    }

    /**
     * One flower layer of a lily pad. Each pad decides for itself whether it has a flower, when it opens and whether
     * it's pink (from its position), so this can't be a plain biome colour.
     */
    private static BlockTintSource lilyFlowerTint(int layer) {
        return new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                return SeasonalColors.lilyFlowerHidden(layer);
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                int pad = level.getBlockTint(pos, LILY_PAD_RESOLVER);
                double flowering = (level.getBlockTint(pos, LILY_BLOOM_RESOLVER) & 0xFF) / 255.0;
                return SeasonalColors.lilyFlower(pad, flowering, layer, pos.getX(), pos.getZ());
            }
        };
    }

    /** In the inventory: the normal colour. In the world: the colour from our resolver. */
    private static BlockTintSource seasonalTint(int inventoryColor, ColorResolver resolver) {
        return new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                return inventoryColor;
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return level.getBlockTint(pos, resolver);
            }
        };
    }

    /**
     * Every tick, check whether the colours should change (new day, /season set, or the setting was switched).
     * If so, clear the colour caches and redraw the chunks around the player.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        ClimateSnapshot.updateClient(SeasonAccess.clientYearPosition(), ClimateSnapshot.dayPosition(minecraft.level));
        if (SeasonalColorState.update(SeasonAccess.clientYearPosition(), ClientConfig.seasonalColors(),
                ClientConfig.seasonalPoplars(), ClientConfig.seasonalWaterLilies())) {
            minecraft.level.clearTintCaches();
            minecraft.levelExtractor.allChanged();
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        SeasonalColorState.reset();
        ClimateSnapshot.clearClient();
        BiomeZones.clear();
    }
}
