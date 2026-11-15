package net.skross04.wildalmanac.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
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

    /** Like the biome's foliage colour, but starting from birch green (and turning golden in autumn). */
    public static final ColorResolver BIRCH_RESOLVER =
            (biome, x, z) -> SeasonalColorState.birch(biome, FoliageColor.FOLIAGE_BIRCH);

    /** Custom colour resolvers must be registered, so Minecraft gives them a colour cache. */
    @SubscribeEvent
    public static void registerColorResolvers(RegisterColorHandlersEvent.ColorResolvers event) {
        event.register(BIRCH_RESOLVER);
    }

    /** Replace birch leaves' fixed colour with our seasonal one. */
    @SubscribeEvent
    public static void registerBlockTints(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                return FoliageColor.FOLIAGE_BIRCH; // in the inventory: normal birch green
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return level.getBlockTint(pos, BIRCH_RESOLVER);
            }
        }), Blocks.BIRCH_LEAVES);
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
