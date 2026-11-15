package net.skross04.wildalmanac.ecology.season;

/**
 * Wild Almanac's own calendar: counts how many ticks of "season time" have passed.
 * <p>
 * It follows the world's day clock, but only its <b>natural</b> movement:
 * <ul>
 *     <li>normal ticking counts (also when NeoForge's {@code /neoforge day} makes days faster or slower),</li>
 *     <li>skipping the night by sleeping counts,</li>
 *     <li>any other jump, like {@code /time set} or {@code /time add}, is ignored,</li>
 *     <li>if the clock stops (game rule {@code advance_time} off), the seasons stop too.</li>
 * </ul>
 * Only Wild Almanac's own season command changes the season directly.
 */
public final class SeasonCalendar {
    /** One Minecraft day is 24000 ticks (20 minutes). */
    public static final long TICKS_PER_DAY = 24000L;

    /**
     * The biggest clock step per tick that still counts as normal ticking. Vanilla moves 1 per tick;
     * a sped-up day rate moves a bit more. Anything bigger is a time command (unless someone slept).
     */
    public static final long MAX_NATURAL_STEP = 100L;

    private long elapsedTicks;
    private long lastClockTicks;
    private boolean started;

    /** A calendar that hasn't seen the world yet; the first {@link #update} starts it at the world's age. */
    public SeasonCalendar() {
    }

    /** A calendar loaded from saved data. */
    public SeasonCalendar(long elapsedTicks, long lastClockTicks) {
        this.elapsedTicks = elapsedTicks;
        this.lastClockTicks = lastClockTicks;
        this.started = true;
    }

    /**
     * Call once per server tick with the world's day clock.
     *
     * @param clockTicks the overworld day clock's total ticks
     * @param slept      true if players skipped the night by sleeping since the last update
     * @return true if anything changed (so it needs saving)
     */
    public boolean update(long clockTicks, boolean slept) {
        if (!started) {
            // First time Wild Almanac runs in this world: start from the world's age.
            // A new world is at 0 (day 1 of spring); an old world continues where its year would be.
            elapsedTicks = Math.max(0L, clockTicks);
            lastClockTicks = clockTicks;
            started = true;
            return true;
        }

        long step = clockTicks - lastClockTicks;
        lastClockTicks = clockTicks;
        if (step > 0 && (step <= MAX_NATURAL_STEP || slept)) {
            elapsedTicks += step;
        }
        return step != 0;
    }

    /** Days passed since the calendar started (0 = the very first day). */
    public long dayIndex() {
        return Math.floorDiv(elapsedTicks, TICKS_PER_DAY);
    }

    /**
     * How far through the year we are, from 0 (start of spring) to just under 1 (end of winter).
     * Includes the time of day, so the climate curve moves smoothly instead of once per day.
     */
    public double yearPosition(int yearLength) {
        long yearTicks = yearLength * TICKS_PER_DAY;
        return (double) Math.floorMod(elapsedTicks, yearTicks) / yearTicks;
    }

    public SeasonDate date(int yearLength) {
        return SeasonDate.fromDay(dayIndex(), yearLength);
    }

    /** Jumps to a day of the current year (1 = first day of spring), keeping the year and time of day. */
    public void setDayOfYear(int dayOfYear, int yearLength) {
        if (dayOfYear < 1 || dayOfYear > yearLength) {
            throw new IllegalArgumentException("Day of year must be between 1 and " + yearLength + ", got " + dayOfYear);
        }
        long year = date(yearLength).year();
        long timeOfDay = Math.floorMod(elapsedTicks, TICKS_PER_DAY);
        long dayIndex = (year - 1) * yearLength + (dayOfYear - 1);
        elapsedTicks = Math.max(0L, dayIndex * TICKS_PER_DAY + timeOfDay);
    }

    /** Jumps to the first day of a season in the current year. */
    public void setSeason(Season season, int yearLength) {
        setDayOfYear(season.ordinal() * (yearLength / 4) + 1, yearLength);
    }

    public long elapsedTicks() {
        return elapsedTicks;
    }

    public long lastClockTicks() {
        return lastClockTicks;
    }

    public boolean isStarted() {
        return started;
    }
}
