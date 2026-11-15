package net.skross04.wildalmanac.datagen.lang;

import net.minecraft.data.PackOutput;
import net.skross04.wildalmanac.ecology.season.Season;
import net.skross04.wildalmanac.item.ModCreativeModeTabs;
import net.skross04.wildalmanac.item.ModItems;
import net.skross04.wildalmanac.season.SeasonTranslations;

/** Spanish (es_es). */
public class EsEsLanguageProvider extends ModLanguageProvider {
    public EsEsLanguageProvider(PackOutput output) {
        super(output, "es_es");
    }

    @Override
    protected void addTranslations() {
        add(ModCreativeModeTabs.WILD_ALMANAC_TAB_TITLE, "Wild Almanac");

        addItem(ModItems.SEASON_CALENDAR, "Calendario de estaciones");
        add(SeasonTranslations.CALENDAR_TOOLTIP, "Clic derecho para ver la estación y el día");
        add(SeasonTranslations.CALENDAR_DATE, "%1$s – día %2$s de %3$s, año %4$s");

        addSeason(Season.SPRING, "Principios de primavera", "Mediados de primavera", "Finales de primavera");
        addSeason(Season.SUMMER, "Principios de verano", "Mediados de verano", "Finales de verano");
        addSeason(Season.AUTUMN, "Principios de otoño", "Mediados de otoño", "Finales de otoño");
        addSeason(Season.WINTER, "Principios de invierno", "Mediados de invierno", "Finales de invierno");
    }
}
