package net.skross04.wildlifeandseasons.datagen.lang;

import net.minecraft.data.PackOutput;
import net.skross04.wildlifeandseasons.climate.ClimateTranslations;
import net.skross04.wildlifeandseasons.climate.Instruments;
import net.skross04.wildlifeandseasons.compat.CompatTranslations;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.season.Season;
import net.skross04.wildlifeandseasons.block.ModBlocks;
import net.skross04.wildlifeandseasons.item.ModCreativeModeTabs;
import net.skross04.wildlifeandseasons.item.ModItems;
import net.skross04.wildlifeandseasons.season.SeasonTranslations;

/** Dutch (nl_nl). */
public class NlNlLanguageProvider extends ModLanguageProvider {
    public NlNlLanguageProvider(PackOutput output) {
        super(output, "nl_nl");
    }

    @Override
    protected void addTranslations() {
        add(ModCreativeModeTabs.WILDLIFE_AND_SEASONS_TAB_TITLE, "Wildlife & Seasons");

        addBlock(ModBlocks.DORMANT_FLOWER, "Slapende bloem");
        addBlock(ModBlocks.ICICLE, "IJspegel");
        addItem(ModItems.THERMOMETER, "Thermometer");
        add(Instruments.THERMOMETER_TOOLTIP, "Rechtsklik om hier de temperatuur af te lezen");
        addBlock(ModBlocks.RAIN_GAUGE, "Regenmeter");
        addBlock(ModBlocks.SNOWDROP, "Sneeuwklokje");
        addBlock(ModBlocks.CROCUS, "Krokus");
        addBlock(ModBlocks.POTTED_SNOWDROP, "Sneeuwklokje in pot");
        addBlock(ModBlocks.POTTED_CROCUS, "Krokus in pot");
        add(Instruments.THERMOMETER_READING, "Thermometer: %s °C");
        add(Instruments.RAIN_GAUGE_READING, "Regenmeter: %1$s%% vol · %2$s%% neerslag dit seizoen");
        add(CompatTranslations.JEI_CALENDAR, "Rechtsklik om de datum en het seizoen van vandaag te lezen. In jungles en savannes zie je ook of het regenseizoen of het droge seizoen is.");
        add(CompatTranslations.JEI_THERMOMETER, "Rechtsklik om de temperatuur te lezen waar je staat. Die verandert met het seizoen, het tijdstip, het bioom en de hoogte.");
        add(CompatTranslations.JEI_RAIN_GAUGE, "Zet hem onder de open hemel. Hij loopt vol als het erop regent (sneller bij onweer) en droogt langzaam uit op warme dagen; bevroren water blijft. Rechtsklik om hem af te lezen. Een comparator ernaast geeft een signaal van 0 tot 15.");
        add(CompatTranslations.JEI_ICICLE, "Groeit in de winter onder de rand van een besneeuwd dak met lucht eronder, en smelt in de lente of naast een fakkel. Hak hem met Zijden aanraking om hem te houden. IJspegels die je zelf plaatst smelten nooit.");
        add(CompatTranslations.JEI_CROP_ALL_YEAR, "Groeit het hele jaar in een kas (glas erboven) en in jungles.");
        add(CompatTranslations.JADE_IN_SEASON, "In het seizoen");
        add(CompatTranslations.JADE_OUT_OF_SEASON, "Buiten het seizoen");
        add(CompatTranslations.JADE_GREENHOUSE, "Kas: groeit het hele jaar");
        add(CompatTranslations.JADE_CONFIG, "Seizoenen van gewassen");
        addItem(ModItems.SEASON_CALENDAR, "Seizoenskalender");
        add(SeasonTranslations.CALENDAR_TOOLTIP, "Rechtsklik om het seizoen en de dag te zien");
        add(SeasonTranslations.WET_SEASON, "Regenseizoen · %s");
        add(SeasonTranslations.DRY_SEASON, "Droog seizoen · %s");
        add(SeasonTranslations.CALENDAR_DATE, "%1$s – dag %2$s van %3$s, jaar %4$s");
        add(SeasonTranslations.COMMAND_SET, "Seizoen ingesteld: %s");

        addSeason(Season.SPRING, "Begin lente", "Midden lente", "Eind lente");
        addSeason(Season.SUMMER, "Begin zomer", "Midden zomer", "Eind zomer");
        addSeason(Season.AUTUMN, "Begin herfst", "Midden herfst", "Eind herfst");
        addSeason(Season.WINTER, "Begin winter", "Midden winter", "Eind winter");

        add(ClimateTranslations.COMMAND_CLIMATE, "%1$s – %2$s °C, neerslag %3$s%%, daglicht %4$s%%");
        add(ClimateTranslations.COMMAND_NO_SEASONS, "Hier zijn geen seizoenen");
        add(ClimateTranslations.zoneKey(ClimateZone.TEMPERATE), "Gematigde zone");
        add(ClimateTranslations.zoneKey(ClimateZone.COLD), "Koude zone");
        add(ClimateTranslations.zoneKey(ClimateZone.TROPICAL), "Tropische zone");
        add(ClimateTranslations.zoneKey(ClimateZone.SAVANNA), "Savannezone");
        add(ClimateTranslations.zoneKey(ClimateZone.ARID), "Droge zone");
        add(ClimateTranslations.zoneKey(ClimateZone.OCEAN), "Zee & kust");
        add(ClimateTranslations.zoneKey(ClimateZone.NO_SEASONS), "Geen seizoenen");

        add(SeasonTranslations.seasonNameKey(Season.SPRING), "Lente");
        add(SeasonTranslations.seasonNameKey(Season.SUMMER), "Zomer");
        add(SeasonTranslations.seasonNameKey(Season.AUTUMN), "Herfst");
        add(SeasonTranslations.seasonNameKey(Season.WINTER), "Winter");
        add(SeasonTranslations.CROP_GROWS_IN, "Groeit in:");
    }
}
