package net.skross04.wildalmanac.datagen.lang;

import net.minecraft.data.PackOutput;
import net.skross04.wildalmanac.climate.ClimateTranslations;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;
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
        add(SeasonTranslations.COMMAND_SET, "Saison réglée : %s");

        addSeason(Season.SPRING, "Début du printemps", "Mi-printemps", "Fin du printemps");
        addSeason(Season.SUMMER, "Début de l'été", "Mi-été", "Fin de l'été");
        addSeason(Season.AUTUMN, "Début de l'automne", "Mi-automne", "Fin de l'automne");
        addSeason(Season.WINTER, "Début de l'hiver", "Mi-hiver", "Fin de l'hiver");

        add(ClimateTranslations.COMMAND_CLIMATE, "%1$s – %2$s °C, précipitations %3$s %%, jour %4$s %%");
        add(ClimateTranslations.COMMAND_NO_SEASONS, "Il n'y a pas de saisons ici");
        add(ClimateTranslations.zoneKey(ClimateZone.TEMPERATE), "Zone tempérée");
        add(ClimateTranslations.zoneKey(ClimateZone.COLD), "Zone froide");
        add(ClimateTranslations.zoneKey(ClimateZone.TROPICAL), "Zone tropicale");
        add(ClimateTranslations.zoneKey(ClimateZone.SAVANNA), "Zone de savane");
        add(ClimateTranslations.zoneKey(ClimateZone.ARID), "Zone aride");
        add(ClimateTranslations.zoneKey(ClimateZone.OCEAN), "Océan et côte");
        add(ClimateTranslations.zoneKey(ClimateZone.NO_SEASONS), "Sans saisons");

        add(SeasonTranslations.seasonNameKey(Season.SPRING), "Printemps");
        add(SeasonTranslations.seasonNameKey(Season.SUMMER), "Été");
        add(SeasonTranslations.seasonNameKey(Season.AUTUMN), "Automne");
        add(SeasonTranslations.seasonNameKey(Season.WINTER), "Hiver");
        add(SeasonTranslations.CROP_GROWS_IN, "Pousse en :");
    }
}
