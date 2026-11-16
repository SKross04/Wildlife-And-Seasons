package net.skross04.wildlifeandseasons.datagen;

import java.util.List;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.OffsetPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.block.ModBlocks;
import net.skross04.wildlifeandseasons.util.ModTags;

/**
 * World generation: patches of snowdrops in woods and crocuses in meadows and grassland. Since 26.3 a feature holds
 * what it places, and the placed feature says where and how many (vanilla's flower patches work the same way).
 * Outside their bloom time the seasonal flower rules make them wilt, and they come back at the same spot.
 */
public final class ModWorldgen {
    public static final ResourceKey<Feature> SNOWDROP = featureKey("snowdrop");
    public static final ResourceKey<Feature> CROCUS = featureKey("crocus");

    public static final ResourceKey<PlacedFeature> SNOWDROP_PATCH = placedKey("snowdrop_patch");
    public static final ResourceKey<PlacedFeature> CROCUS_PATCH = placedKey("crocus_patch");

    public static final ResourceKey<BiomeModifier> ADD_SNOWDROPS = modifierKey("add_snowdrops");
    public static final ResourceKey<BiomeModifier> ADD_CROCUSES = modifierKey("add_crocuses");

    private ModWorldgen() {
    }

    public static void bootstrapFeatures(BootstrapContext<Feature> context) {
        context.register(SNOWDROP, new SimpleBlockFeature(BlockStateProvider.of(ModBlocks.SNOWDROP.get())));
        context.register(CROCUS, new SimpleBlockFeature(BlockStateProvider.of(ModBlocks.CROCUS.get())));
    }

    public static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<Feature> features = context.lookup(Registries.FEATURE);
        // Snowdrops grow in dense carpets: a patch in about one chunk in six.
        PlacementUtils.register(context, SNOWDROP_PATCH, features.getOrThrow(SNOWDROP), patch(6, 48, 4));
        // Crocuses: wider, looser patches in about one chunk in five.
        PlacementUtils.register(context, CROCUS_PATCH, features.getOrThrow(CROCUS), patch(5, 40, 6));
    }

    public static void bootstrapBiomeModifiers(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);
        context.register(ADD_SNOWDROPS, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_SNOWDROPS), HolderSet.direct(placed.getOrThrow(SNOWDROP_PATCH)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_CROCUSES, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_CROCUSES), HolderSet.direct(placed.getOrThrow(CROCUS_PATCH)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
    }

    /**
     * A flower patch like vanilla's: once every {@code rarity} chunks, {@code tries} flowers spread around one spot
     * (up to {@code spread} blocks away), only on air (the feature checks the flower can stand there).
     */
    private static List<PlacementModifier> patch(int rarity, int tries, int spread) {
        return List.of(
                RarityFilter.onAverageOnceEvery(rarity),
                InSquarePlacement.spread(),
                // Ground level ignoring leaves, so the flowers land on the forest floor and not on the treetops.
                HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES),
                BiomeFilter.biome(),
                CountPlacement.of(tries),
                OffsetPlacement.ofTriangle(spread, 2),
                BlockPredicateFilter.forPredicate(BlockPredicate.matchesTag(BlockTags.AIR)));
    }

    private static ResourceKey<Feature> featureKey(String name) {
        return ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(WildlifeAndSeasons.MODID, name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(WildlifeAndSeasons.MODID, name));
    }

    private static ResourceKey<BiomeModifier> modifierKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(WildlifeAndSeasons.MODID, name));
    }
}
