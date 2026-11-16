package net.skross04.wildlifeandseasons.datagen;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.minecraft.core.registries.Registries;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.datagen.lang.DeDeLanguageProvider;
import net.skross04.wildlifeandseasons.datagen.lang.EnUsLanguageProvider;
import net.skross04.wildlifeandseasons.datagen.lang.EsEsLanguageProvider;
import net.skross04.wildlifeandseasons.datagen.lang.FrFrLanguageProvider;
import net.skross04.wildlifeandseasons.datagen.lang.NlNlLanguageProvider;
import net.skross04.wildlifeandseasons.datagen.lang.SvSeLanguageProvider;

/**
 * Runs when you start the Gradle task {@code runData}. Every provider writes JSON files
 * into {@code src/generated/resources}, so we never write models, recipes or lang files by hand.
 */
@EventBusSubscriber(modid = WildlifeAndSeasons.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        event.createProvider(ModModelProvider::new);
        event.createProvider(ModDataMapProvider::new);
        // Biomes live in the "world" registries, so the tag provider needs that lookup.
        event.addProvider(new ModBiomeTagProvider(event.getGenerator().getPackOutput(), event.getWorldLookupProvider()));
        event.addProvider(new ModBlockTagProvider(event.getGenerator().getPackOutput(), event.getReloadableLookupProvider()));

        event.createProvider(EnUsLanguageProvider::new);
        event.createProvider(FrFrLanguageProvider::new);
        event.createProvider(EsEsLanguageProvider::new);
        event.createProvider(DeDeLanguageProvider::new);
        event.createProvider(NlNlLanguageProvider::new);
        event.createProvider(SvSeLanguageProvider::new);
    }

    /** Since 26.3, recipes (and loot tables, advancements) are generated as registry entries through this event. */
    @SubscribeEvent
    public static void gatherRegistryEntries(GatherDataRegistryEntriesEvent event) {
        event.recipe(ModRecipeProvider::new);
        event.lootTable(new LootTableProvider.SubProviderEntry(ModBlockLootProvider::new, LootContextParamSets.BLOCK));
        // World generation (snowdrop and crocus patches): features, where they're placed, and which biomes get them.
        event.add(Registries.FEATURE, ModWorldgen::bootstrapFeatures)
                .add(Registries.PLACED_FEATURE, ModWorldgen::bootstrapPlacedFeatures)
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModWorldgen::bootstrapBiomeModifiers);
    }
}
