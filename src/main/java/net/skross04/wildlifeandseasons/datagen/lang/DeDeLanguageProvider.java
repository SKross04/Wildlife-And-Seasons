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

/** German (de_de). */
public class DeDeLanguageProvider extends ModLanguageProvider {
    public DeDeLanguageProvider(PackOutput output) {
        super(output, "de_de");
    }

    @Override
    protected void addTranslations() {
        add(ModCreativeModeTabs.WILDLIFE_AND_SEASONS_TAB_TITLE, "Wildlife & Seasons");

        addBlock(ModBlocks.DORMANT_FLOWER, "Ruhende Blume");
        addBlock(ModBlocks.ICICLE, "Eiszapfen");
        addItem(ModItems.THERMOMETER, "Thermometer");
        add(Instruments.THERMOMETER_TOOLTIP, "Rechtsklick, um die Temperatur hier abzulesen");
        addBlock(ModBlocks.RAIN_GAUGE, "Regenmesser");
        add(Instruments.THERMOMETER_READING, "Thermometer: %s °C");
        add(Instruments.RAIN_GAUGE_READING, "Regenmesser: %1$s%% voll · %2$s%% Niederschlag in dieser Jahreszeit");
        add(CompatTranslations.JEI_CALENDAR, "Rechtsklick zeigt das heutige Datum und die Jahreszeit. In Dschungeln und Savannen zeigt er auch, ob Regenzeit oder Trockenzeit ist.");
        add(CompatTranslations.JEI_THERMOMETER, "Rechtsklick zeigt die Temperatur an deinem Standort. Sie ändert sich mit der Jahreszeit, der Tageszeit, dem Biom und der Höhe.");
        add(CompatTranslations.JEI_RAIN_GAUGE, "Unter freien Himmel stellen. Er füllt sich, wenn es auf ihn regnet (schneller bei Gewitter), und trocknet an warmen Tagen langsam aus; gefrorenes Wasser bleibt. Rechtsklick zum Ablesen. Ein Komparator daneben gibt ein Signal von 0 bis 15.");
        add(CompatTranslations.JEI_ICICLE, "Bildet sich im Winter unter dem Rand eines verschneiten Dachs mit freier Luft darunter und schmilzt im Frühling oder neben einer Fackel. Mit Behutsamkeit abbauen, um ihn zu behalten. Eiszapfen, die du platzierst, schmelzen nie.");
        add(CompatTranslations.JEI_CROP_ALL_YEAR, "Sie wächst das ganze Jahr in einem Gewächshaus (Glas darüber) und im Dschungel.");
        add(CompatTranslations.JADE_IN_SEASON, "Hat Saison");
        add(CompatTranslations.JADE_OUT_OF_SEASON, "Keine Saison");
        add(CompatTranslations.JADE_GREENHOUSE, "Gewächshaus: wächst das ganze Jahr");
        add(CompatTranslations.JADE_CONFIG, "Jahreszeiten der Pflanzen");
        addItem(ModItems.SEASON_CALENDAR, "Jahreszeitenkalender");
        add(SeasonTranslations.CALENDAR_TOOLTIP, "Rechtsklick, um Jahreszeit und Tag zu sehen");
        add(SeasonTranslations.WET_SEASON, "Regenzeit · %s");
        add(SeasonTranslations.DRY_SEASON, "Trockenzeit · %s");
        add(SeasonTranslations.CALENDAR_DATE, "%1$s – Tag %2$s von %3$s, Jahr %4$s");
        add(SeasonTranslations.COMMAND_SET, "Jahreszeit gesetzt: %s");

        addSeason(Season.SPRING, "Frühlingsanfang", "Frühlingsmitte", "Frühlingsende");
        addSeason(Season.SUMMER, "Sommeranfang", "Sommermitte", "Sommerende");
        addSeason(Season.AUTUMN, "Herbstanfang", "Herbstmitte", "Herbstende");
        addSeason(Season.WINTER, "Winteranfang", "Wintermitte", "Winterende");

        add(ClimateTranslations.COMMAND_CLIMATE, "%1$s – %2$s °C, Niederschlag %3$s %%, Tageslicht %4$s %%");
        add(ClimateTranslations.COMMAND_NO_SEASONS, "Hier gibt es keine Jahreszeiten");
        add(ClimateTranslations.zoneKey(ClimateZone.TEMPERATE), "Gemäßigte Zone");
        add(ClimateTranslations.zoneKey(ClimateZone.COLD), "Kalte Zone");
        add(ClimateTranslations.zoneKey(ClimateZone.TROPICAL), "Tropische Zone");
        add(ClimateTranslations.zoneKey(ClimateZone.SAVANNA), "Savannenzone");
        add(ClimateTranslations.zoneKey(ClimateZone.ARID), "Trockenzone");
        add(ClimateTranslations.zoneKey(ClimateZone.OCEAN), "Meer & Küste");
        add(ClimateTranslations.zoneKey(ClimateZone.NO_SEASONS), "Keine Jahreszeiten");

        add(SeasonTranslations.seasonNameKey(Season.SPRING), "Frühling");
        add(SeasonTranslations.seasonNameKey(Season.SUMMER), "Sommer");
        add(SeasonTranslations.seasonNameKey(Season.AUTUMN), "Herbst");
        add(SeasonTranslations.seasonNameKey(Season.WINTER), "Winter");
        add(SeasonTranslations.CROP_GROWS_IN, "Wächst im:");
    }
}
