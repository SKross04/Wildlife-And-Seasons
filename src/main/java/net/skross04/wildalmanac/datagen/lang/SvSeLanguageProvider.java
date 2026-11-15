package net.skross04.wildalmanac.datagen.lang;

import net.minecraft.data.PackOutput;
import net.skross04.wildalmanac.climate.ClimateTranslations;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;
import net.skross04.wildalmanac.ecology.season.Season;
import net.skross04.wildalmanac.item.ModCreativeModeTabs;
import net.skross04.wildalmanac.item.ModItems;
import net.skross04.wildalmanac.season.SeasonTranslations;

/** Swedish (sv_se). */
public class SvSeLanguageProvider extends ModLanguageProvider {
    public SvSeLanguageProvider(PackOutput output) {
        super(output, "sv_se");
    }

    @Override
    protected void addTranslations() {
        add(ModCreativeModeTabs.WILD_ALMANAC_TAB_TITLE, "Wild Almanac");

        addItem(ModItems.SEASON_CALENDAR, "Årstidskalender");
        add(SeasonTranslations.CALENDAR_TOOLTIP, "Högerklicka för att se årstid och dag");
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
