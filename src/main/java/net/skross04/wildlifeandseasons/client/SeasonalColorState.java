package net.skross04.wildlifeandseasons.client;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import org.jspecify.annotations.Nullable;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.skross04.wildlifeandseasons.climate.BiomeZones;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.climate.SeasonalColors;
import net.skross04.wildlifeandseasons.mixin.BiomeAccessor;
import net.skross04.wildlifeandseasons.util.ModTags;

/**
 * The seasonal colours currently shown on this client.
 * <p>
 * Chunks are drawn on several threads at once, so everything here is thread-safe:
 * the zone cache lives in {@link BiomeZones}, and the current tints are a volatile array.
 */
public final class SeasonalColorState {
    /** Current tint per climate zone (index = zone ordinal); null = no seasonal colours (off, or season unknown). */
    private static volatile SeasonalColors.Tint[] tints;
    /** Seasonal poplars and Dappled Forest grass (client setting {@code seasonalPoplars}). */
    private static volatile boolean poplars = true;

    private SeasonalColorState() {
    }

    public static int grass(Biome biome, int base) {
        SeasonalColors.Tint tint = tintFor(biome);
        if (tint == null) {
            return base;
        }
        if (BiomeZones.of(biome).autumnColored()) {
            // The biome's own grass colour is its autumn colour: green until autumn (or Mojang's look when switched off).
            return poplars ? SeasonalColors.autumnBiomeGrass(grassGreen(biome), base, tint) : base;
        }
        return SeasonalColors.grass(base, tint);
    }

    /** Leaves without a known position (modded leaves using the biome colour): one autumn colour. */
    public static int foliage(Biome biome, int base) {
        SeasonalColors.Tint tint = tintFor(biome);
        if (tint == null) {
            return base;
        }
        if (BiomeZones.of(biome).autumnColored()) {
            if (!poplars) {
                return base;
            }
            base = ((BiomeAccessor) (Object) biome).wildlifeandseasons$getFoliageColorFromTexture();
        }
        return SeasonalColors.foliage(base, tint);
    }

    /** Poplar leaves: green until autumn, then their own colour (Mojang's colour all year when switched off). */
    public static int poplar(Biome biome, SeasonalColors.Poplar kind, int x, int z) {
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null || !poplars ? kind.autumnColor() : SeasonalColors.poplar(tint, kind, x, z);
    }

    /**
     * The colour for a poplar's falling-leaf particle, or empty to keep Mojang's own red/orange/yellow leaf particle
     * (while the tree shows its autumn colour, or when seasonal poplars are off).
     */
    public static OptionalInt poplarParticleColor(Biome biome, int poplarColor, int x, int z) {
        SeasonalColors.Tint tint = tintFor(biome);
        if (tint == null || !poplars || SeasonalColors.poplarInAutumnColor(tint, x, z)) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(SeasonalColors.poplarParticleColor(poplarColor));
    }

    public static SeasonalColors.@Nullable Poplar poplarKind(BlockState state) {
        if (state.is(Blocks.RED_POPLAR_LEAVES)) {
            return SeasonalColors.Poplar.RED;
        }
        if (state.is(Blocks.ORANGE_POPLAR_LEAVES)) {
            return SeasonalColors.Poplar.ORANGE;
        }
        if (state.is(Blocks.YELLOW_POPLAR_LEAVES)) {
            return SeasonalColors.Poplar.YELLOW;
        }
        return null;
    }

    private static int grassGreen(Biome biome) {
        return ((BiomeAccessor) (Object) biome).wildlifeandseasons$getGrassColorFromTexture();
    }

    /** Vanilla leaves at a position: each patch of trees gets its own autumn colour and timing. */
    public static int leaves(Biome biome, int x, int z) {
        int base = vanillaFoliage(biome);
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? base : SeasonalColors.foliage(base, tint, x, z);
    }

    public static int darkOak(Biome biome, int x, int z) {
        int base = vanillaFoliage(biome);
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? base : SeasonalColors.darkOak(base, tint, x, z);
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

    public static int azalea(Biome biome) {
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? SeasonalColors.azaleaLeaves() : SeasonalColors.azalea(tint);
    }

    public static int azaleaFlowers(Biome biome) {
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? SeasonalColors.azaleaFlowerColor() : SeasonalColors.azaleaFlowers(tint);
    }

    public static int paleOak(Biome biome) {
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? SeasonalColors.paleOakLeaves() : SeasonalColors.paleOak(tint);
    }

    public static int mangrove(Biome biome, int base) {
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? base : SeasonalColors.mangrove(base, tint);
    }

    /** Spruce needles: green all year, snowy in winter. */
    public static int evergreen(Biome biome, int base) {
        SeasonalColors.Tint tint = tintFor(biome);
        return tint == null ? base : SeasonalColors.evergreen(base, tint);
    }

    /**
     * How many more (or fewer) leaves fall than vanilla: lots in autumn, few the rest of the year.
     * Only for leaves that drop in autumn (block tag {@code wildlifeandseasons:drops_leaf_litter}) and cherry petals
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
    public static boolean update(OptionalDouble yearPosition, boolean enabled, boolean seasonalPoplars) {
        boolean poplarsChanged = poplars != seasonalPoplars;
        poplars = seasonalPoplars;
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
            return poplarsChanged && next != null;
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

    /**
     * The biome's leaf colour before our seasonal change (same logic as vanilla's getFoliageColor). In a biome whose
     * own leaf colour is an autumn colour (Dappled Forest), the normal green instead, so autumn comes with the season.
     */
    private static int vanillaFoliage(Biome biome) {
        if (poplars && BiomeZones.of(biome).autumnColored()) {
            return ((BiomeAccessor) (Object) biome).wildlifeandseasons$getFoliageColorFromTexture();
        }
        return biome.getModifiedSpecialEffects().foliageColorOverride()
                .orElseGet(() -> ((BiomeAccessor) (Object) biome).wildlifeandseasons$getFoliageColorFromTexture());
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
