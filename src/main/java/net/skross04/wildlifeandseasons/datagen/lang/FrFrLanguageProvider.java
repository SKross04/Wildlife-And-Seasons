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

/** French (fr_fr). */
public class FrFrLanguageProvider extends ModLanguageProvider {
    public FrFrLanguageProvider(PackOutput output) {
        super(output, "fr_fr");
    }

    @Override
    protected void addTranslations() {
        add(ModCreativeModeTabs.WILDLIFE_AND_SEASONS_TAB_TITLE, "Wildlife & Seasons");

        addBlock(ModBlocks.DORMANT_FLOWER, "Fleur en dormance");
        addBlock(ModBlocks.ICICLE, "Stalactite de glace");
        addItem(ModItems.THERMOMETER, "Thermomètre");
        add(Instruments.THERMOMETER_TOOLTIP, "Clic droit pour lire la température ici");
        addBlock(ModBlocks.RAIN_GAUGE, "Pluviomètre");
        addBlock(ModBlocks.SNOWDROP, "Perce-neige");
        addBlock(ModBlocks.CROCUS, "Crocus");
        addBlock(ModBlocks.POTTED_SNOWDROP, "Perce-neige en pot");
        addBlock(ModBlocks.POTTED_CROCUS, "Crocus en pot");
        addBlock(ModBlocks.DESERT_SUNFLOWER, "Tournesol du désert");
        addBlock(ModBlocks.SAND_VERBENA, "Abronie des sables");
        addBlock(ModBlocks.DUNE_PRIMROSE, "Onagre des dunes");
        addBlock(ModBlocks.POTTED_DESERT_SUNFLOWER, "Tournesol du désert en pot");
        addBlock(ModBlocks.POTTED_SAND_VERBENA, "Abronie des sables en pot");
        addBlock(ModBlocks.POTTED_DUNE_PRIMROSE, "Onagre des dunes en pot");
        add(Instruments.THERMOMETER_READING, "Thermomètre : %s °C");
        add(Instruments.RAIN_GAUGE_READING, "Pluviomètre : rempli à %1$s %% · %2$s %% de précipitations cette saison");
        add(CompatTranslations.JEI_CALENDAR, "Clic droit pour lire la date et la saison du jour. Dans les jungles et les savanes, il indique aussi s'il s'agit de la saison des pluies ou de la saison sèche.");
        add(CompatTranslations.JEI_THERMOMETER, "Clic droit pour lire la température là où vous êtes. Elle change avec la saison, l'heure, le biome et l'altitude.");
        add(CompatTranslations.JEI_RAIN_GAUGE, "Placez-le à ciel ouvert. Il se remplit quand il pleut dessus (plus vite pendant un orage) et sèche lentement par temps chaud ; l'eau gelée reste. Clic droit pour le lire. Un comparateur à côté donne un signal de 0 à 15.");
        add(CompatTranslations.JEI_ICICLE, "Se forme en hiver sous le bord d'un toit enneigé avec du vide en dessous, et fond au printemps ou près d'une torche. Minez-la avec Toucher de soie pour la garder. Les stalactites de glace que vous posez ne fondent jamais.");
        add(CompatTranslations.JEI_CROP_ALL_YEAR, "Elle pousse toute l'année dans une serre (du verre au-dessus) et dans les jungles.");
        add(CompatTranslations.JADE_IN_SEASON, "De saison");
        add(CompatTranslations.JADE_OUT_OF_SEASON, "Hors saison");
        add(CompatTranslations.JADE_GREENHOUSE, "Serre : pousse toute l'année");
        add(CompatTranslations.JADE_CONFIG, "Saisons des cultures");
        addItem(ModItems.SEASON_CALENDAR, "Calendrier des saisons");
        add(SeasonTranslations.CALENDAR_TOOLTIP, "Clic droit pour voir la saison et le jour");
        add(SeasonTranslations.WET_SEASON, "Saison des pluies · %s");
        add(SeasonTranslations.DRY_SEASON, "Saison sèche · %s");
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
