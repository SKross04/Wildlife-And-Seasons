package net.skross04.wildlifeandseasons.season;

import net.minecraft.network.chat.Component;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.ecology.season.Season;
import net.skross04.wildlifeandseasons.ecology.season.SeasonDate;
import net.skross04.wildlifeandseasons.ecology.season.SubSeason;

/**
 * Translation keys for season names and the calendar text.
 * The same keys are used by the language datagen, so code and lang files can't drift apart.
 */
public final class SeasonTranslations {
    /** "%1$s – day %2$s of %3$s, year %4$s" */
    public static final String CALENDAR_DATE = "item." + WildlifeAndSeasons.MODID + ".season_calendar.date";
    public static final String CALENDAR_TOOLTIP = "item." + WildlifeAndSeasons.MODID + ".season_calendar.tooltip";
    /** "Season set: %s" (feedback of /season set) */
    public static final String COMMAND_SET = "commands." + WildlifeAndSeasons.MODID + ".season.set";

    /** "Wet season · %s" and "Dry season · %s" (calendar in savannas and jungles) */
    public static final String WET_SEASON = "item." + WildlifeAndSeasons.MODID + ".season_calendar.wet_season";
    public static final String DRY_SEASON = "item." + WildlifeAndSeasons.MODID + ".season_calendar.dry_season";
    /** "Grows in:" (the seasons follow on their own lines) */
    public static final String CROP_GROWS_IN = "tooltip." + WildlifeAndSeasons.MODID + ".crop.grows_in";

    private SeasonTranslations() {
    }

    /** Just the season name, e.g. {@code season.wildlifeandseasons.spring}. */
    public static String seasonNameKey(Season season) {
        return "season." + WildlifeAndSeasons.MODID + "." + season.id();
    }

    /**
     * One key per combination (e.g. {@code season.wildlifeandseasons.early_spring}) instead of
     * "early" + "spring", because many languages change the words depending on the season
     * (French "début du printemps", German "Frühlingsanfang").
     */
    public static String seasonKey(SubSeason subSeason, Season season) {
        return "season." + WildlifeAndSeasons.MODID + "." + subSeason.id() + "_" + season.id();
    }

    /** The text the Season Calendar shows, e.g. "Early Spring – day 3 of 25, year 1". */
    public static Component describe(SeasonDate date) {
        return Component.translatable(CALENDAR_DATE,
                Component.translatable(seasonKey(date.subSeason(), date.season())),
                date.dayOfSeason(),
                date.seasonLength(),
                date.year());
    }
}
