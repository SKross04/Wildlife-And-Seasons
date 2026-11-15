package net.skross04.wildalmanac.climate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;

/**
 * Remembers the climate details of each biome (zone, vanilla temperature, ocean offset),
 * so mixins called thousands of times per second don't check tags every time.
 * Thread-safe; cleared when tags reload or a world is left.
 */
public final class BiomeZones {
    /** @param zone climate zone; @param biomeTemperature vanilla temperature value; @param extraOffset extra °C (warm/icy oceans) */
    public record BiomeClimate(ClimateZone zone, double biomeTemperature, double extraOffset) {
    }

    private static final Map<Biome, BiomeClimate> CACHE = new ConcurrentHashMap<>();

    /** Set by client code: the client world's registries (null when no world is loaded). */
    private static volatile Supplier<@Nullable RegistryAccess> clientRegistries = () -> null;

    private BiomeZones() {
    }

    public static BiomeClimate of(Biome biome) {
        BiomeClimate cached = CACHE.get(biome);
        if (cached != null) {
            return cached;
        }
        Holder<Biome> holder = findHolder(biome);
        BiomeClimate climate;
        if (holder != null) {
            ClimateZone zone = WorldClimate.zoneOf(holder);
            climate = new BiomeClimate(zone, biome.getModifiedClimateSettings().temperature(), WorldClimate.extraOffset(zone, holder));
            CACHE.put(biome, climate);
        } else {
            // Not in a loaded registry (yet): use the vanilla values, but don't cache the guess.
            Biome.ClimateSettings settings = biome.getModifiedClimateSettings();
            climate = new BiomeClimate(ClimateZone.fromVanillaClimate(settings.temperature(), settings.downfall()), settings.temperature(), 0.0);
        }
        return climate;
    }

    public static void setClientRegistries(Supplier<@Nullable RegistryAccess> registries) {
        clientRegistries = registries;
    }

    public static void clear() {
        CACHE.clear();
    }

    /**
     * Tags belong to the biome as it is registered. In singleplayer the server and the client each have
     * their own copy of every biome, so look in whichever registry this biome belongs to.
     */
    private static @Nullable Holder<Biome> findHolder(Biome biome) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            Holder<Biome> holder = holderIn(server.registryAccess(), biome);
            if (holder != null) {
                return holder;
            }
        }
        RegistryAccess client = clientRegistries.get();
        return client != null ? holderIn(client, biome) : null;
    }

    private static @Nullable Holder<Biome> holderIn(RegistryAccess access, Biome biome) {
        Registry<Biome> registry = access.lookupOrThrow(Registries.BIOME);
        return registry.getResourceKey(biome).isPresent() ? registry.wrapAsHolder(biome) : null;
    }
}
