package net.skross04.wildalmanac.datagen.lang;

import net.minecraft.data.PackOutput;
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
    }
}
