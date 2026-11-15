package net.skross04.wildlifeandseasons.datagen.lang;

import net.minecraft.data.PackOutput;
import net.skross04.wildlifeandseasons.climate.ClimateTranslations;
import net.skross04.wildlifeandseasons.climate.Instruments;
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
        add(Instruments.THERMOMETER_READING, "Termometer: %s °C");
        add(Instruments.RAIN_GAUGE_READING, "Regnmätare: %1$s%% full · %2$s%% nederbörd denna årstid");
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
