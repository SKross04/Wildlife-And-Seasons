package net.skross04.wildalmanac.datagen.lang;

import net.minecraft.data.PackOutput;
import net.skross04.wildalmanac.climate.ClimateTranslations;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;
import net.skross04.wildalmanac.ecology.season.Season;
import net.skross04.wildalmanac.item.ModCreativeModeTabs;
import net.skross04.wildalmanac.item.ModItems;
import net.skross04.wildalmanac.season.SeasonTranslations;

/** Dutch (nl_nl). */
public class NlNlLanguageProvider extends ModLanguageProvider {
    public NlNlLanguageProvider(PackOutput output) {
        super(output, "nl_nl");
    }

    @Override
    protected void addTranslations() {
        add(ModCreativeModeTabs.WILD_ALMANAC_TAB_TITLE, "Wild Almanac");

        addItem(ModItems.SEASON_CALENDAR, "Seizoenskalender");
        add(SeasonTranslations.CALENDAR_TOOLTIP, "Rechtsklik om het seizoen en de dag te zien");
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
    }
}
