package net.skross04.wildalmanac.datagen.lang;

import net.minecraft.data.PackOutput;
import net.skross04.wildalmanac.climate.ClimateTranslations;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;
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
        add(SeasonTranslations.COMMAND_SET, "Estación cambiada: %s");

        addSeason(Season.SPRING, "Principios de primavera", "Mediados de primavera", "Finales de primavera");
        addSeason(Season.SUMMER, "Principios de verano", "Mediados de verano", "Finales de verano");
        addSeason(Season.AUTUMN, "Principios de otoño", "Mediados de otoño", "Finales de otoño");
        addSeason(Season.WINTER, "Principios de invierno", "Mediados de invierno", "Finales de invierno");

        add(ClimateTranslations.COMMAND_CLIMATE, "%1$s – %2$s °C, lluvia %3$s %%, luz del día %4$s %%");
        add(ClimateTranslations.COMMAND_NO_SEASONS, "Aquí no hay estaciones");
        add(ClimateTranslations.zoneKey(ClimateZone.TEMPERATE), "Zona templada");
        add(ClimateTranslations.zoneKey(ClimateZone.COLD), "Zona fría");
        add(ClimateTranslations.zoneKey(ClimateZone.TROPICAL), "Zona tropical");
        add(ClimateTranslations.zoneKey(ClimateZone.SAVANNA), "Zona de sabana");
        add(ClimateTranslations.zoneKey(ClimateZone.ARID), "Zona árida");
        add(ClimateTranslations.zoneKey(ClimateZone.OCEAN), "Océano y costa");
        add(ClimateTranslations.zoneKey(ClimateZone.NO_SEASONS), "Sin estaciones");
    }
}
