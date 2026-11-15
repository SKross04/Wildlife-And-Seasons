package net.skross04.wildalmanac.season;

import net.minecraft.network.chat.Component;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.ecology.season.Season;
import net.skross04.wildalmanac.ecology.season.SeasonDate;
import net.skross04.wildalmanac.ecology.season.SubSeason;

/**
 * Translation keys for season names and the calendar text.
 * The same keys are used by the language datagen, so code and lang files can't drift apart.
 */
public final class SeasonTranslations {
    /** "%1$s – day %2$s of %3$s, year %4$s" */
    public static final String CALENDAR_DATE = "item." + WildAlmanac.MODID + ".season_calendar.date";
    public static final String CALENDAR_TOOLTIP = "item." + WildAlmanac.MODID + ".season_calendar.tooltip";
    /** "Season set: %s" (feedback of /season set) */
    public static final String COMMAND_SET = "commands." + WildAlmanac.MODID + ".season.set";

    /** "Grows in:" (the seasons follow on their own lines) */
    public static final String CROP_GROWS_IN = "tooltip." + WildAlmanac.MODID + ".crop.grows_in";

    private SeasonTranslations() {
    }

    /** Just the season name, e.g. {@code season.wildalmanac.spring}. */
    public static String seasonNameKey(Season season) {
        return "season." + WildAlmanac.MODID + "." + season.id();
    }

    /**
     * One key per combination (e.g. {@code season.wildalmanac.early_spring}) instead of
     * "early" + "spring", because many languages change the words depending on the season
     * (French "début du printemps", German "Frühlingsanfang").
     */
    public static String seasonKey(SubSeason subSeason, Season season) {
        return "season." + WildAlmanac.MODID + "." + subSeason.id() + "_" + season.id();
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
