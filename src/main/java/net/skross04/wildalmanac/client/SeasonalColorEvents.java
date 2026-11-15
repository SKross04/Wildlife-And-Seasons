package net.skross04.wildalmanac.client;

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
import net.skross04.wildalmanac.ClientConfig;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.climate.BiomeZones;
import net.skross04.wildalmanac.climate.ClimateSnapshot;
import net.skross04.wildalmanac.ecology.climate.SeasonalColors;
import net.skross04.wildalmanac.season.SeasonAccess;

/**
 * Keeps the seasonal grass and leaf colours (and the client's climate snapshot) up to date on the player's screen.
 * Grass and most leaves are handled by {@code BiomeColorMixin}; birch leaves get their own colour here,
 * because vanilla gives them a fixed colour instead of the biome's.
 */
@EventBusSubscriber(modid = WildAlmanac.MODID, value = Dist.CLIENT)
public class SeasonalColorEvents {
    static {
        // Lets BiomeZones find the client world's biome tags (used when playing on someone else's server).
        BiomeZones.setClientRegistries(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            return level == null ? null : level.registryAccess();
        });
    }

    /** Vanilla leaves (oak, dark oak, acacia, jungle, vines): seasonal, with red/orange/yellow patches in autumn. */
    public static final ColorResolver LEAVES_RESOLVER =
            (biome, x, z) -> SeasonalColorState.leaves(biome, Mth.floor(x), Mth.floor(z));

    /** Birch leaves start from birch green and turn golden in autumn. */
    public static final ColorResolver BIRCH_RESOLVER =
            (biome, x, z) -> SeasonalColorState.birch(biome, FoliageColor.FOLIAGE_BIRCH, Mth.floor(x), Mth.floor(z));

    /** Spruce needles: evergreen, but snowy in winter. */
    public static final ColorResolver EVERGREEN_RESOLVER =
            (biome, x, z) -> SeasonalColorState.evergreen(biome, FoliageColor.FOLIAGE_EVERGREEN);

    /** Cherry leaves: pink in spring, green in summer, autumn colours, snowy in winter. */
    public static final ColorResolver CHERRY_RESOLVER =
            (biome, x, z) -> SeasonalColorState.cherry(biome, Mth.floor(x), Mth.floor(z));

    /** Custom colour resolvers must be registered, so Minecraft gives them a colour cache. */
    @SubscribeEvent
    public static void registerColorResolvers(RegisterColorHandlersEvent.ColorResolvers event) {
        event.register(LEAVES_RESOLVER);
        event.register(BIRCH_RESOLVER);
        event.register(EVERGREEN_RESOLVER);
        event.register(CHERRY_RESOLVER);
    }

    /**
     * Give vanilla leaves our colours. This event runs after vanilla registered its own colours, so ours replace them.
     * Falling-leaf particles take their colour from here too.
     */
    @SubscribeEvent
    public static void registerBlockTints(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(seasonalTint(FoliageColor.FOLIAGE_DEFAULT, LEAVES_RESOLVER)),
                Blocks.OAK_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.ACACIA_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.VINE);
        event.register(List.of(seasonalTint(FoliageColor.FOLIAGE_BIRCH, BIRCH_RESOLVER)), Blocks.BIRCH_LEAVES);
        event.register(List.of(seasonalTint(FoliageColor.FOLIAGE_EVERGREEN, EVERGREEN_RESOLVER)), Blocks.SPRUCE_LEAVES);
        // Cherry leaves get a grey texture in our cherry_leaves model, so this colour decides pink or green.
        event.register(List.of(seasonalTint(SeasonalColors.cherryBlossom(), CHERRY_RESOLVER)), Blocks.CHERRY_LEAVES);
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
        if (SeasonalColorState.update(SeasonAccess.clientYearPosition(), ClientConfig.seasonalColors())) {
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
