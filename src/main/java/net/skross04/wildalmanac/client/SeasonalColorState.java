package net.skross04.wildalmanac.client;

import java.util.OptionalDouble;

import net.minecraft.world.level.biome.Biome;
import net.skross04.wildalmanac.climate.BiomeZones;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;
import net.skross04.wildalmanac.ecology.climate.SeasonalColors;

/**
 * The seasonal colours currently shown on this client.
 * <p>
 * Chunks are drawn on several threads at once, so everything here is thread-safe:
 * the zone cache lives in {@link BiomeZones}, and the current tints are a volatile array.
 */
public final class SeasonalColorState {
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
        return BiomeZones.of(biome).zone();
    }
}
