package net.skross04.wildalmanac.datagen.lang;

import net.minecraft.data.PackOutput;
import net.skross04.wildalmanac.climate.ClimateTranslations;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;
import net.skross04.wildalmanac.ecology.season.Season;
import net.skross04.wildalmanac.item.ModCreativeModeTabs;
import net.skross04.wildalmanac.item.ModItems;
import net.skross04.wildalmanac.season.SeasonTranslations;

/** German (de_de). */
public class DeDeLanguageProvider extends ModLanguageProvider {
    public DeDeLanguageProvider(PackOutput output) {
        super(output, "de_de");
    }

    @Override
    protected void addTranslations() {
        add(ModCreativeModeTabs.WILD_ALMANAC_TAB_TITLE, "Wild Almanac");

        addItem(ModItems.SEASON_CALENDAR, "Jahreszeitenkalender");
        add(SeasonTranslations.CALENDAR_TOOLTIP, "Rechtsklick, um Jahreszeit und Tag zu sehen");
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
