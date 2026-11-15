package net.skross04.wildalmanac.datagen.lang;

import net.minecraft.data.PackOutput;
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

        addSeason(Season.SPRING, "Begin lente", "Midden lente", "Eind lente");
        addSeason(Season.SUMMER, "Begin zomer", "Midden zomer", "Eind zomer");
        addSeason(Season.AUTUMN, "Begin herfst", "Midden herfst", "Eind herfst");
        addSeason(Season.WINTER, "Begin winter", "Midden winter", "Eind winter");
    }
}
