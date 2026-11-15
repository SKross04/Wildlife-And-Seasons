package net.skross04.wildalmanac.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.datagen.lang.DeDeLanguageProvider;
import net.skross04.wildalmanac.datagen.lang.EnUsLanguageProvider;
import net.skross04.wildalmanac.datagen.lang.EsEsLanguageProvider;
import net.skross04.wildalmanac.datagen.lang.FrFrLanguageProvider;
import net.skross04.wildalmanac.datagen.lang.NlNlLanguageProvider;
import net.skross04.wildalmanac.datagen.lang.SvSeLanguageProvider;

/**
 * Runs when you start the Gradle task {@code runData}. Every provider writes JSON files
 * into {@code src/generated/resources}, so we never write models, recipes or lang files by hand.
 */
@EventBusSubscriber(modid = WildAlmanac.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        event.createProvider(ModModelProvider::new);

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
    }
}
