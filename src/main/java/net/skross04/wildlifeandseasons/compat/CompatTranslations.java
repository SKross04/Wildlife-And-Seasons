package net.skross04.wildlifeandseasons.compat;

import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.ecology.season.Season;
import net.skross04.wildlifeandseasons.season.SeasonTranslations;

/**
 * Texts for the optional integrations (JEI info pages, Jade tooltips). Kept apart from the integration classes,
 * so the language datagen can use them without JEI or Jade being installed.
 */
public final class CompatTranslations {
    private static final String JEI = "jei." + WildlifeAndSeasons.MODID + ".info.";
    public static final String JEI_CALENDAR = JEI + "season_calendar";
    public static final String JEI_THERMOMETER = JEI + "thermometer";
    public static final String JEI_RAIN_GAUGE = JEI + "rain_gauge";
    public static final String JEI_ICICLE = JEI + "icicle";
    public static final String JEI_CROP_ALL_YEAR = JEI + "crop_all_year";

    private static final String JADE = "jade." + WildlifeAndSeasons.MODID + ".";
    public static final String JADE_IN_SEASON = JADE + "in_season";
    public static final String JADE_OUT_OF_SEASON = JADE + "out_of_season";
    public static final String JADE_GREENHOUSE = JADE + "greenhouse";
    /** Jade's own key for the on/off switch of our tooltip in its settings screen. */
    public static final String JADE_CONFIG = "config.jade.plugin_" + WildlifeAndSeasons.MODID + ".crop_season";

    private CompatTranslations() {
    }

    /** "Grows in: Spring, Summer" (season names in the player's language). */
    public static MutableComponent growsIn(Set<Season> seasons) {
        MutableComponent text = Component.translatable(SeasonTranslations.CROP_GROWS_IN);
        boolean first = true;
        for (Season season : Season.values()) {
            if (seasons.contains(season)) {
                text.append(first ? " " : ", ").append(Component.translatable(SeasonTranslations.seasonNameKey(season)));
                first = false;
            }
        }
        return text;
    }
}
