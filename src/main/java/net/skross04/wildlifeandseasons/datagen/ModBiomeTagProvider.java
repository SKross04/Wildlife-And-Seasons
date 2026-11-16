package net.skross04.wildlifeandseasons.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.util.ModTags;

/**
 * Sorts biomes into climate zones. Uses the common {@code c:} tags where possible,
 * so modded biomes that use those tags get the right zone automatically.
 * Biomes in no zone tag fall back to their vanilla temperature and rainfall (see ClimateZone).
 */
public class ModBiomeTagProvider extends BiomeTagsProvider {
    public ModBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, WildlifeAndSeasons.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(ModTags.Biomes.climateZone(ClimateZone.NO_SEASONS))
                .addTags(Tags.Biomes.IS_NETHER, Tags.Biomes.IS_END, Tags.Biomes.IS_VOID,
                        // caves (lush caves, dripstone caves, deep dark): no seasons underground
                        Tags.Biomes.IS_UNDERGROUND);

        // Empty on purpose: the file exists so modpack makers can see it and add biomes.
        tag(ModTags.Biomes.climateZone(ClimateZone.TEMPERATE));

        tag(ModTags.Biomes.climateZone(ClimateZone.OCEAN))
                .addTags(Tags.Biomes.IS_OCEAN, Tags.Biomes.IS_BEACH)
                .add(Biomes.STONY_SHORE);

        tag(ModTags.Biomes.climateZone(ClimateZone.ARID))
                .addTags(Tags.Biomes.IS_DESERT, Tags.Biomes.IS_BADLANDS);

        tag(ModTags.Biomes.climateZone(ClimateZone.SAVANNA))
                .addTag(Tags.Biomes.IS_SAVANNA);

        tag(ModTags.Biomes.climateZone(ClimateZone.TROPICAL))
                .addTag(Tags.Biomes.IS_JUNGLE)
                .add(Biomes.MANGROVE_SWAMP);

        tag(ModTags.Biomes.climateZone(ClimateZone.COLD))
                .addTag(Tags.Biomes.IS_COLD_OVERWORLD);

        tag(ModTags.Biomes.HAS_LEAF_LITTER)
                .addTag(Tags.Biomes.IS_FOREST);
    }
}
