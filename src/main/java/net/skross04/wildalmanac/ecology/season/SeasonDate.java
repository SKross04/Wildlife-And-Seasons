package net.skross04.wildalmanac.ecology.season;

/**
 * A date in the Wild Almanac calendar: which year, season and day it is.
 *
 * @param year        year number, starting at 1
 * @param season      current season
 * @param subSeason   early, mid or late part of the season
 * @param dayOfSeason day within the season, starting at 1
 * @param seasonLength number of days in one season
 * @param dayOfYear   day within the year, starting at 1
 */
public record SeasonDate(long year, Season season, SubSeason subSeason, int dayOfSeason, int seasonLength, int dayOfYear) {

    /** Default year length: 100 days, so 4 seasons of 25 days. */
    public static final int DEFAULT_YEAR_LENGTH = 100;

    /** Smallest allowed year: 4 seasons of 3 days, so each sub-season gets at least one day. */
    public static final int MIN_YEAR_LENGTH = 12;

    /**
     * Works out the date from a day count.
     *
     * @param dayIndex   days passed since the start of the calendar (0 = day 1 of spring, year 1)
     * @param yearLength days per year; must be a multiple of 4 and at least {@link #MIN_YEAR_LENGTH}
     */
    public static SeasonDate fromDay(long dayIndex, int yearLength) {
        if (yearLength < MIN_YEAR_LENGTH || yearLength % 4 != 0) {
            throw new IllegalArgumentException("Year length must be a multiple of 4 and at least " + MIN_YEAR_LENGTH + ", got " + yearLength);
        }

        // floorDiv/floorMod instead of / and % so negative day counts still give a valid date.
        long year = Math.floorDiv(dayIndex, yearLength) + 1;
        int dayInYear = (int) Math.floorMod(dayIndex, (long) yearLength); // 0-based

        int seasonLength = yearLength / 4;
        Season season = Season.values()[dayInYear / seasonLength];
        int dayInSeason = dayInYear % seasonLength; // 0-based

        return new SeasonDate(year, season, subSeasonFor(dayInSeason, seasonLength), dayInSeason + 1, seasonLength, dayInYear + 1);
    }

    /**
     * Early and late each get a third of the season (rounded down), mid gets the rest.
     * For 25 days: early = days 1-8, mid = days 9-17, late = days 18-25.
     */
    static SubSeason subSeasonFor(int dayInSeason, int seasonLength) {
        int edgeLength = seasonLength / 3;
        if (dayInSeason < edgeLength) {
            return SubSeason.EARLY;
        }
        if (dayInSeason >= seasonLength - edgeLength) {
            return SubSeason.LATE;
        }
        return SubSeason.MID;
    }
}
