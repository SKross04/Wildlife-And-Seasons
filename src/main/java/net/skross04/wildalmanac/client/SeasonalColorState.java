package net.skross04.wildalmanac.client;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.skross04.wildalmanac.climate.WorldClimate;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;
import net.skross04.wildalmanac.ecology.climate.SeasonalColors;

/**
 * The seasonal colours currently shown on this client.
 * <p>
 * Chunks are drawn on several threads at once, so everything here is thread-safe:
 * a concurrent map for the zone cache and a volatile array for the current tints.
 */
public final class SeasonalColorState {
    /** Climate zone per biome, so tags aren't checked again for every grass block. */
    private static final Map<Biome, ClimateZone> ZONES = new ConcurrentHashMap<>();
    /** Current tint per climate zone (index = zone ordinal); null = no seasonal colours (off, or season unknown). */
    private static volatile SeasonalColors.Tint[] tints;

    private SeasonalColorState() {
    }

    public static int grass(Biome biome, int base) {
        SeasonalColors.Tint[] current = tints;
        return current == null ? base : SeasonalColors.grass(base, current[zoneOf(biome).ordinal()]);
    }

    public static int foliage(Biome biome, int base) {
        SeasonalColors.Tint[] current = tints;
        return current == null ? base : SeasonalColors.foliage(base, current[zoneOf(biome).ordinal()]);
    }

    public static int birch(Biome biome, int base) {
        SeasonalColors.Tint[] current = tints;
        return current == null ? base : SeasonalColors.birch(base, current[zoneOf(biome).ordinal()]);
    }

    /**
     * Recalculates the tints for the current point in the year.
     *
     * @return true if the colours changed enough that the world should be redrawn
     */
    public static boolean update(OptionalDouble yearPosition, boolean enabled) {
        SeasonalColors.Tint[] next = null;
        if (enabled && yearPosition.isPresent()) {
            ClimateZone[] zones = ClimateZone.values();
            next = new SeasonalColors.Tint[zones.length];
            for (ClimateZone zone : zones) {
                next[zone.ordinal()] = SeasonalColors.tint(zone, yearPosition.getAsDouble());
            }
        }
        SeasonalColors.Tint[] previous = tints;
        if (!changed(previous, next)) {
            return false;
        }
        tints = next;
        return true;
    }

    /** Forget everything when leaving a world (another world can have other biomes and tags). */
    public static void reset() {
        ZONES.clear();
        tints = null;
    }

    private static boolean changed(SeasonalColors.Tint[] previous, SeasonalColors.Tint[] next) {
        if (previous == null || next == null) {
            return previous != next;
        }
        for (int i = 0; i < next.length; i++) {
            if (next[i].differsFrom(previous[i])) {
                return true;
            }
        }
        return false;
    }

    private static ClimateZone zoneOf(Biome biome) {
        ClimateZone cached = ZONES.get(biome);
        if (cached != null) {
            return cached;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return ClimateZone.TEMPERATE; // no world loaded: don't cache a guess
        }
        Holder<Biome> holder = level.registryAccess().lookupOrThrow(Registries.BIOME).wrapAsHolder(biome);
        ClimateZone zone = WorldClimate.zoneOf(holder);
        ZONES.put(biome, zone);
        return zone;
    }
}
