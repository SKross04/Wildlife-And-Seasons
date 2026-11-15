package net.skross04.wildalmanac.datagen.lang;

import net.minecraft.data.PackOutput;
import net.skross04.wildalmanac.ecology.season.Season;
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

        addItem(ModItems.SEASON_CALENDAR, "Season Calendar");
        add(SeasonTranslations.CALENDAR_TOOLTIP, "Right-click to see the season and day");
        add(SeasonTranslations.CALENDAR_DATE, "%1$s – day %2$s of %3$s, year %4$s");
        add(SeasonTranslations.COMMAND_SET, "Season set: %s");

        addSeason(Season.SPRING, "Early Spring", "Mid Spring", "Late Spring");
        addSeason(Season.SUMMER, "Early Summer", "Mid Summer", "Late Summer");
        addSeason(Season.AUTUMN, "Early Autumn", "Mid Autumn", "Late Autumn");
        addSeason(Season.WINTER, "Early Winter", "Mid Winter", "Late Winter");
    }
}
