package net.skross04.wildalmanac.datagen.lang;

import net.minecraft.data.PackOutput;
import net.skross04.wildalmanac.ecology.season.Season;
import net.skross04.wildalmanac.item.ModCreativeModeTabs;
import net.skross04.wildalmanac.item.ModItems;
import net.skross04.wildalmanac.season.SeasonTranslations;

/** French (fr_fr). */
public class FrFrLanguageProvider extends ModLanguageProvider {
    public FrFrLanguageProvider(PackOutput output) {
        super(output, "fr_fr");
    }

    @Override
    protected void addTranslations() {
        add(ModCreativeModeTabs.WILD_ALMANAC_TAB_TITLE, "Wild Almanac");

        addItem(ModItems.SEASON_CALENDAR, "Calendrier des saisons");
        add(SeasonTranslations.CALENDAR_TOOLTIP, "Clic droit pour voir la saison et le jour");
        add(SeasonTranslations.CALENDAR_DATE, "%1$s – jour %2$s sur %3$s, année %4$s");

        addSeason(Season.SPRING, "Début du printemps", "Mi-printemps", "Fin du printemps");
        addSeason(Season.SUMMER, "Début de l'été", "Mi-été", "Fin de l'été");
        addSeason(Season.AUTUMN, "Début de l'automne", "Mi-automne", "Fin de l'automne");
        addSeason(Season.WINTER, "Début de l'hiver", "Mi-hiver", "Fin de l'hiver");
    }
}
