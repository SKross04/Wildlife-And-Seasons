package net.skross04.wildalmanac.datagen.lang;

import net.minecraft.data.PackOutput;
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

        addSeason(Season.SPRING, "Frühlingsanfang", "Frühlingsmitte", "Frühlingsende");
        addSeason(Season.SUMMER, "Sommeranfang", "Sommermitte", "Sommerende");
        addSeason(Season.AUTUMN, "Herbstanfang", "Herbstmitte", "Herbstende");
        addSeason(Season.WINTER, "Winteranfang", "Wintermitte", "Winterende");
    }
}
