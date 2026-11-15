package net.skross04.wildalmanac.client;

import java.util.OptionalDouble;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.skross04.wildalmanac.climate.BiomeZones;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;
import net.skross04.wildalmanac.ecology.climate.SeasonalColors;
import net.skross04.wildalmanac.mixin.BiomeAccessor;
import net.skross04.wildalmanac.util.ModTags;

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
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? base : SeasonalColors.grass(base, tint);
    }

    /** Leaves without a known position (modded leaves using the biome colour): one autumn colour. */
    public static int foliage(Biome biome, int base) {
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? base : SeasonalColors.foliage(base, tint);
    }

    /** Vanilla leaves at a position: red, orange or yellow autumn patches. */
    public static int leaves(Biome biome, int x, int z) {
        int base = vanillaFoliage(biome);
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? base : SeasonalColors.foliage(base, tint, x, z);
    }

    public static int birch(Biome biome, int base, int x, int z) {
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? base : SeasonalColors.birch(base, tint, x, z);
    }

    public static int cherry(Biome biome, int x, int z) {
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? SeasonalColors.cherryBlossom() : SeasonalColors.cherry(tint, x, z);
    }

    /** How strongly it's autumn for this biome's trees right now (0–1), for wind and falling leaves. */
    public static double leafAutumn(Biome biome) {
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? 0 : tint.leafAutumn();
    }

    /** How snowy this biome's trees look right now (0–1). */
    public static double snowOnLeaves(Biome biome) {
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? 0 : tint.snow();
    }

    /** Spruce needles: green all year, snowy in winter. */
    public static int evergreen(Biome biome, int base) {
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? base : SeasonalColors.evergreen(base, tint);
    }

    /**
     * How many more (or fewer) leaves fall than vanilla: lots in autumn, few the rest of the year.
     * Only for leaves that drop in autumn (block tag {@code wildalmanac:drops_leaf_litter}) and cherry petals
     * (only in spring); other leaves stay vanilla.
     */
    public static float fallingLeavesFactor(Biome biome, BlockState leaves) {
        SeasonalColors.Tint tint = tintFor(biome);
        if (tint == null) {
            return 1.0F;
        }
        if (leaves.is(Blocks.CHERRY_LEAVES)) {
            return (float) (0.05 + 1.5 * tint.spring()); // cherry petals fall while it blossoms, in spring
        }
        if (!leaves.is(ModTags.Blocks.DROPS_LEAF_LITTER)) {
            return 1.0F;
        }
        return (float) (0.3 + 7.0 * tint.leafAutumn());
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

    /** Forget everything when leaving a world. */
    public static void reset() {
        tints = null;
    }

    private static SeasonalColors.Tint tintFor(Biome biome) {
        SeasonalColors.Tint[] current = tints;
        return current == null ? null : current[BiomeZones.of(biome).zone().ordinal()];
    }

    /** The biome's leaf colour before our seasonal change (same logic as vanilla's getFoliageColor). */
    private static int vanillaFoliage(Biome biome) {
        return biome.getModifiedSpecialEffects().foliageColorOverride()
                .orElseGet(() -> ((BiomeAccessor) (Object) biome).wildalmanac$getFoliageColorFromTexture());
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
}
