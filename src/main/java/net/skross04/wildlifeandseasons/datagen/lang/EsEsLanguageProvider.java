package net.skross04.wildlifeandseasons.datagen.lang;

import net.minecraft.data.PackOutput;
import net.skross04.wildlifeandseasons.climate.ClimateTranslations;
import net.skross04.wildlifeandseasons.climate.Instruments;
import net.skross04.wildlifeandseasons.compat.CompatTranslations;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.season.Season;
import net.skross04.wildlifeandseasons.block.ModBlocks;
import net.skross04.wildlifeandseasons.item.ModCreativeModeTabs;
import net.skross04.wildlifeandseasons.item.ModItems;
import net.skross04.wildlifeandseasons.season.SeasonTranslations;

/** Spanish (es_es). */
public class EsEsLanguageProvider extends ModLanguageProvider {
    public EsEsLanguageProvider(PackOutput output) {
        super(output, "es_es");
    }

    @Override
    protected void addTranslations() {
        add(ModCreativeModeTabs.WILDLIFE_AND_SEASONS_TAB_TITLE, "Wildlife & Seasons");

        addBlock(ModBlocks.DORMANT_FLOWER, "Flor latente");
        addBlock(ModBlocks.ICICLE, "Carámbano");
        addItem(ModItems.THERMOMETER, "Termómetro");
        add(Instruments.THERMOMETER_TOOLTIP, "Clic derecho para leer la temperatura aquí");
        addBlock(ModBlocks.RAIN_GAUGE, "Pluviómetro");
        addBlock(ModBlocks.SNOWDROP, "Campanilla de invierno");
        addBlock(ModBlocks.CROCUS, "Azafrán de primavera");
        addBlock(ModBlocks.POTTED_SNOWDROP, "Campanilla de invierno en maceta");
        addBlock(ModBlocks.POTTED_CROCUS, "Azafrán de primavera en maceta");
        addBlock(ModBlocks.DESERT_SUNFLOWER, "Girasol del desierto");
        addBlock(ModBlocks.SAND_VERBENA, "Verbena de arena");
        addBlock(ModBlocks.DUNE_PRIMROSE, "Onagra de las dunas");
        addBlock(ModBlocks.POTTED_DESERT_SUNFLOWER, "Girasol del desierto en maceta");
        addBlock(ModBlocks.POTTED_SAND_VERBENA, "Verbena de arena en maceta");
        addBlock(ModBlocks.POTTED_DUNE_PRIMROSE, "Onagra de las dunas en maceta");
        addBlock(ModBlocks.LICHEN, "Liquen");
        add(Instruments.THERMOMETER_READING, "Termómetro: %s °C");
        add(Instruments.RAIN_GAUGE_READING, "Pluviómetro: lleno al %1$s%% · %2$s%% de lluvia esta estación");
        add(CompatTranslations.JEI_CALENDAR, "Clic derecho para ver la fecha y la estación de hoy. En selvas y sabanas también indica si es la estación lluviosa o la seca.");
        add(CompatTranslations.JEI_THERMOMETER, "Clic derecho para ver la temperatura donde estás. Cambia con la estación, la hora del día, el bioma y la altura.");
        add(CompatTranslations.JEI_RAIN_GAUGE, "Colócalo a cielo abierto. Se llena cuando le llueve encima (más rápido con tormenta) y se seca poco a poco en días cálidos; el agua helada se queda. Clic derecho para leerlo. Un comparador al lado da una señal de 0 a 15.");
        add(CompatTranslations.JEI_ICICLE, "Se forma en invierno bajo el borde de un tejado nevado con aire debajo, y se derrite en primavera o junto a una antorcha. Pícalo con Toque de seda para conservarlo. Los carámbanos que colocas nunca se derriten.");
        add(CompatTranslations.JEI_CROP_ALL_YEAR, "Crece todo el año en un invernadero (con cristal encima) y en las selvas.");
        add(CompatTranslations.JADE_IN_SEASON, "De temporada");
        add(CompatTranslations.JADE_OUT_OF_SEASON, "Fuera de temporada");
        add(CompatTranslations.JADE_GREENHOUSE, "Invernadero: crece todo el año");
        add(CompatTranslations.JADE_CONFIG, "Estaciones de cultivos");
        addItem(ModItems.SEASON_CALENDAR, "Calendario de estaciones");
        add(SeasonTranslations.CALENDAR_TOOLTIP, "Clic derecho para ver la estación y el día");
        add(SeasonTranslations.WET_SEASON, "Estación lluviosa · %s");
        add(SeasonTranslations.DRY_SEASON, "Estación seca · %s");
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

        add(SeasonTranslations.seasonNameKey(Season.SPRING), "Primavera");
        add(SeasonTranslations.seasonNameKey(Season.SUMMER), "Verano");
        add(SeasonTranslations.seasonNameKey(Season.AUTUMN), "Otoño");
        add(SeasonTranslations.seasonNameKey(Season.WINTER), "Invierno");
        add(SeasonTranslations.CROP_GROWS_IN, "Crece en:");
    }
}
