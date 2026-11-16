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

/** English (source) (en_us). */
public class EnUsLanguageProvider extends ModLanguageProvider {
    public EnUsLanguageProvider(PackOutput output) {
        super(output, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ModCreativeModeTabs.WILDLIFE_AND_SEASONS_TAB_TITLE, "Wildlife & Seasons");

        addBlock(ModBlocks.DORMANT_FLOWER, "Dormant Flower");
        addBlock(ModBlocks.ICICLE, "Icicle");
        addItem(ModItems.THERMOMETER, "Thermometer");
        add(Instruments.THERMOMETER_TOOLTIP, "Right-click to read the temperature here");
        addBlock(ModBlocks.RAIN_GAUGE, "Rain Gauge");
        addBlock(ModBlocks.SNOWDROP, "Snowdrop");
        addBlock(ModBlocks.CROCUS, "Crocus");
        addBlock(ModBlocks.POTTED_SNOWDROP, "Potted Snowdrop");
        addBlock(ModBlocks.POTTED_CROCUS, "Potted Crocus");
        add(Instruments.THERMOMETER_READING, "Thermometer: %s °C");
        add(Instruments.RAIN_GAUGE_READING, "Rain gauge: %1$s%% full · %2$s%% rainfall this season");
        add(CompatTranslations.JEI_CALENDAR, "Right-click to read today's date and season. In jungles and savannas it also tells you whether it's the wet or the dry season.");
        add(CompatTranslations.JEI_THERMOMETER, "Right-click to read the temperature where you stand. It changes with the season, the time of day, the biome and the height.");
        add(CompatTranslations.JEI_RAIN_GAUGE, "Place it under the open sky. It fills up while it rains on it (faster in a thunderstorm) and slowly dries out on warm days; frozen water stays. Right-click to read it. A comparator next to it gives a signal from 0 to 15.");
        add(CompatTranslations.JEI_ICICLE, "Grows in winter under the edge of a snowy roof with open air below it, and melts in spring or next to a torch. Mine it with Silk Touch to keep it. Icicles you place never melt.");
        add(CompatTranslations.JEI_CROP_ALL_YEAR, "It grows all year in a greenhouse (glass above it) and in jungles.");
        add(CompatTranslations.JADE_IN_SEASON, "In season");
        add(CompatTranslations.JADE_OUT_OF_SEASON, "Out of season");
        add(CompatTranslations.JADE_GREENHOUSE, "Greenhouse: grows all year");
        add(CompatTranslations.JADE_CONFIG, "Crop seasons");
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
