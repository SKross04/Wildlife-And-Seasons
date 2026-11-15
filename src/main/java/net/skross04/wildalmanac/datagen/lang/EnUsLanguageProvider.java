package net.skross04.wildalmanac.datagen.lang;

import net.minecraft.data.PackOutput;
import net.skross04.wildalmanac.climate.ClimateTranslations;
import net.skross04.wildalmanac.climate.Instruments;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;
import net.skross04.wildalmanac.ecology.season.Season;
import net.skross04.wildalmanac.block.ModBlocks;
import net.skross04.wildalmanac.item.ModCreativeModeTabs;
import net.skross04.wildalmanac.item.ModItems;
import net.skross04.wildalmanac.season.SeasonTranslations;

/** English (source) (en_us). */
public class EnUsLanguageProvider extends ModLanguageProvider {
    public EnUsLanguageProvider(PackOutput output) {
        super(output, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ModCreativeModeTabs.WILD_ALMANAC_TAB_TITLE, "Wild Almanac");

        addBlock(ModBlocks.DORMANT_FLOWER, "Dormant Flower");
        addBlock(ModBlocks.ICICLE, "Icicle");
        addItem(ModItems.THERMOMETER, "Thermometer");
        add(Instruments.THERMOMETER_TOOLTIP, "Right-click to read the temperature here");
        addBlock(ModBlocks.RAIN_GAUGE, "Rain Gauge");
        add(Instruments.THERMOMETER_READING, "Thermometer: %s °C");
        add(Instruments.RAIN_GAUGE_READING, "Rain gauge: %1$s%% full · %2$s%% rainfall this season");
        addItem(ModItems.SEASON_CALENDAR, "Season Calendar");
        add(SeasonTranslations.CALENDAR_TOOLTIP, "Right-click to see the season and day");
        add(SeasonTranslations.WET_SEASON, "Wet season · %s");
        add(SeasonTranslations.DRY_SEASON, "Dry season · %s");
        add(SeasonTranslations.CALENDAR_DATE, "%1$s – day %2$s of %3$s, year %4$s");
        add(SeasonTranslations.COMMAND_SET, "Season set: %s");

        addSeason(Season.SPRING, "Early Spring", "Mid Spring", "Late Spring");
        addSeason(Season.SUMMER, "Early Summer", "Mid Summer", "Late Summer");
        addSeason(Season.AUTUMN, "Early Autumn", "Mid Autumn", "Late Autumn");
        addSeason(Season.WINTER, "Early Winter", "Mid Winter", "Late Winter");

        add(ClimateTranslations.COMMAND_CLIMATE, "%1$s – %2$s °C, rainfall %3$s%%, daylight %4$s%%");
        add(ClimateTranslations.COMMAND_NO_SEASONS, "There are no seasons here");
        add(ClimateTranslations.zoneKey(ClimateZone.TEMPERATE), "Temperate zone");
        add(ClimateTranslations.zoneKey(ClimateZone.COLD), "Cold zone");
        add(ClimateTranslations.zoneKey(ClimateZone.TROPICAL), "Tropical zone");
        add(ClimateTranslations.zoneKey(ClimateZone.SAVANNA), "Savanna zone");
        add(ClimateTranslations.zoneKey(ClimateZone.ARID), "Arid zone");
        add(ClimateTranslations.zoneKey(ClimateZone.OCEAN), "Ocean & coast");
        add(ClimateTranslations.zoneKey(ClimateZone.NO_SEASONS), "No seasons");

        add(SeasonTranslations.seasonNameKey(Season.SPRING), "Spring");
        add(SeasonTranslations.seasonNameKey(Season.SUMMER), "Summer");
        add(SeasonTranslations.seasonNameKey(Season.AUTUMN), "Autumn");
        add(SeasonTranslations.seasonNameKey(Season.WINTER), "Winter");
        add(SeasonTranslations.CROP_GROWS_IN, "Grows in:");
    }
}
