package net.skross04.wildlifeandseasons.ecology.season;

/**
 * Each season is split into an early, mid and late part.
 * With the default 25-day season that's 8 / 9 / 8 days.
 * These are only labels for the calendar; the climate itself changes smoothly.
 */
public enum SubSeason {
    EARLY,
    MID,
    LATE;

    /** Lowercase name used in translation keys, e.g. {@code "early"}. */
    public String id() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
}
