package net.skross04.wildlifeandseasons.datagen.lang;

import net.minecraft.data.PackOutput;
import net.skross04.wildlifeandseasons.datagen.ModAdvancements;
import net.skross04.wildlifeandseasons.climate.ClimateTranslations;
import net.skross04.wildlifeandseasons.climate.Instruments;
import net.skross04.wildlifeandseasons.compat.CompatTranslations;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.season.Season;
import net.skross04.wildlifeandseasons.block.ModBlocks;
import net.skross04.wildlifeandseasons.item.ModCreativeModeTabs;
import net.skross04.wildlifeandseasons.item.ModItems;
import net.skross04.wildlifeandseasons.season.SeasonTranslations;

/** Swedish (sv_se). */
public class SvSeLanguageProvider extends ModLanguageProvider {
    public SvSeLanguageProvider(PackOutput output) {
        super(output, "sv_se");
    }

    @Override
    protected void addTranslations() {
        add(ModCreativeModeTabs.WILDLIFE_AND_SEASONS_TAB_TITLE, "Wildlife & Seasons");

        addBlock(ModBlocks.DORMANT_FLOWER, "Vilande blomma");
        addBlock(ModBlocks.ICICLE, "Istapp");
        addItem(ModItems.THERMOMETER, "Termometer");
        add(Instruments.THERMOMETER_TOOLTIP, "Högerklicka för att läsa av temperaturen här");
        addBlock(ModBlocks.RAIN_GAUGE, "Regnmätare");
        addBlock(ModBlocks.SNOWDROP, "Snödroppe");
        addBlock(ModBlocks.CROCUS, "Krokus");
        addBlock(ModBlocks.POTTED_SNOWDROP, "Krukad snödroppe");
        addBlock(ModBlocks.POTTED_CROCUS, "Krukad krokus");
        addBlock(ModBlocks.DESERT_SUNFLOWER, "Ökensolros");
        addBlock(ModBlocks.SAND_VERBENA, "Sandverbena");
        addBlock(ModBlocks.DUNE_PRIMROSE, "Dynnattljus");
        addBlock(ModBlocks.POTTED_DESERT_SUNFLOWER, "Krukad ökensolros");
        addBlock(ModBlocks.POTTED_SAND_VERBENA, "Krukad sandverbena");
        addBlock(ModBlocks.POTTED_DUNE_PRIMROSE, "Krukat dynnattljus");
        addBlock(ModBlocks.LICHEN, "Lav");
        // Advancements
        add(ModAdvancements.titleKey("root"), "Wildlife & Seasons");
        add(ModAdvancements.descriptionKey("root"), "Se världen förändras under året");
        add(ModAdvancements.titleKey("know_the_season"), "Känn årstiden");
        add(ModAdvancements.descriptionKey("know_the_season"), "Skaffa en årstidskalender");
        add(ModAdvancements.titleKey("snowed_in"), "Insnöad");
        add(ModAdvancements.descriptionKey("snowed_in"), "Var ute när det snöar");
        add(ModAdvancements.titleKey("year_in_the_wild"), "Ett år i det vilda");
        add(ModAdvancements.descriptionKey("year_in_the_wild"), "Spela ett helt år");
        add(ModAdvancements.titleKey("leaf_peeper"), "Höstfärger");
        add(ModAdvancements.descriptionKey("leaf_peeper"), "Besök en skog när höstfärgerna är som vackrast");
        add(ModAdvancements.titleKey("hanami"), "Hanami");
        add(ModAdvancements.descriptionKey("hanami"), "Se körsbärsträden i full blom");
        add(ModAdvancements.titleKey("winter_harvest"), "Vinterskörd");
        add(ModAdvancements.descriptionKey("winter_harvest"), "Skörda en mogen gröda på vintern");
        add(ModAdvancements.titleKey("first_signs_of_spring"), "Vårtecken");
        add(ModAdvancements.descriptionKey("first_signs_of_spring"), "Plocka en snödroppe eller en krokus");
        add(ModAdvancements.titleKey("superbloom"), "Superblomning");
        add(ModAdvancements.descriptionKey("superbloom"), "Samla alla tre ökenblommor");
        add(ModAdvancements.titleKey("mushroom_season"), "Svampsäsong");
        add(ModAdvancements.descriptionKey("mushroom_season"), "Plocka en svamp som kom upp på hösten");
        add(ModAdvancements.titleKey("scraped_clean"), "Skrapad ren");
        add(ModAdvancements.descriptionKey("scraped_clean"), "Skrapa bort mossan från ett block med en yxa");
        add(ModAdvancements.titleKey("brr"), "Brr!");
        add(ModAdvancements.descriptionKey("brr"), "Läs av en termometer under noll");
        add(ModAdvancements.titleKey("rain_check"), "Regnmätare");
        add(ModAdvancements.descriptionKey("rain_check"), "Placera en regnmätare");
        add(Instruments.THERMOMETER_READING, "Termometer: %s °C");
        add(Instruments.RAIN_GAUGE_READING, "Regnmätare: %1$s%% full · %2$s%% nederbörd denna årstid");
        add(CompatTranslations.JEI_CALENDAR, "Högerklicka för att se dagens datum och årstid. I djungler och savanner visar den också om det är regnperiod eller torrperiod.");
        add(CompatTranslations.JEI_THERMOMETER, "Högerklicka för att läsa av temperaturen där du står. Den ändras med årstiden, tiden på dygnet, biomet och höjden.");
        add(CompatTranslations.JEI_RAIN_GAUGE, "Ställ den under bar himmel. Den fylls när det regnar på den (snabbare vid åska) och torkar långsamt ut under varma dagar; fruset vatten stannar. Högerklicka för att läsa av den. En komparator bredvid ger en signal från 0 till 15.");
        add(CompatTranslations.JEI_ICICLE, "Bildas på vintern under kanten av ett snötäckt tak med luft under, och smälter på våren eller nära en fackla. Bryt den med Silkesberöring för att behålla den. Istappar som du placerar smälter aldrig.");
        add(CompatTranslations.JEI_CROP_ALL_YEAR, "Den växer hela året i ett växthus (glas ovanför) och i djungler.");
        add(CompatTranslations.JADE_IN_SEASON, "I säsong");
        add(CompatTranslations.JADE_OUT_OF_SEASON, "Utanför säsong");
        add(CompatTranslations.JADE_GREENHOUSE, "Växthus: växer hela året");
        add(CompatTranslations.JADE_CONFIG, "Grödornas årstider");
        addItem(ModItems.SEASON_CALENDAR, "Årstidskalender");
        add(SeasonTranslations.CALENDAR_TOOLTIP, "Högerklicka för att se årstid och dag");
        add(SeasonTranslations.WET_SEASON, "Regnperiod · %s");
        add(SeasonTranslations.DRY_SEASON, "Torrperiod · %s");
        add(SeasonTranslations.CALENDAR_DATE, "%1$s – dag %2$s av %3$s, år %4$s");
        add(SeasonTranslations.COMMAND_SET, "Årstid ändrad: %s");

        addSeason(Season.SPRING, "Tidig vår", "Mitten av våren", "Sen vår");
        addSeason(Season.SUMMER, "Tidig sommar", "Mitten av sommaren", "Sen sommar");
        addSeason(Season.AUTUMN, "Tidig höst", "Mitten av hösten", "Sen höst");
        addSeason(Season.WINTER, "Tidig vinter", "Mitten av vintern", "Sen vinter");

        add(ClimateTranslations.COMMAND_CLIMATE, "%1$s – %2$s °C, nederbörd %3$s %%, dagsljus %4$s %%");
        add(ClimateTranslations.COMMAND_NO_SEASONS, "Här finns inga årstider");
        add(ClimateTranslations.zoneKey(ClimateZone.TEMPERATE), "Tempererad zon");
        add(ClimateTranslations.zoneKey(ClimateZone.COLD), "Kall zon");
        add(ClimateTranslations.zoneKey(ClimateZone.TROPICAL), "Tropisk zon");
        add(ClimateTranslations.zoneKey(ClimateZone.SAVANNA), "Savannzon");
        add(ClimateTranslations.zoneKey(ClimateZone.ARID), "Torr zon");
        add(ClimateTranslations.zoneKey(ClimateZone.OCEAN), "Hav & kust");
        add(ClimateTranslations.zoneKey(ClimateZone.NO_SEASONS), "Inga årstider");

        add(SeasonTranslations.seasonNameKey(Season.SPRING), "Vår");
        add(SeasonTranslations.seasonNameKey(Season.SUMMER), "Sommar");
        add(SeasonTranslations.seasonNameKey(Season.AUTUMN), "Höst");
        add(SeasonTranslations.seasonNameKey(Season.WINTER), "Vinter");
        add(SeasonTranslations.CROP_GROWS_IN, "Växer under:");
    }
}
