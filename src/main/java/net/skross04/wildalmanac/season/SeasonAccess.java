package net.skross04.wildalmanac.season;

import java.util.Optional;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.skross04.wildalmanac.ecology.season.Season;
import net.skross04.wildalmanac.ecology.season.SeasonCalendar;
import net.skross04.wildalmanac.ecology.season.SeasonDate;

/**
 * The current season on either side: the server reads its saved calendar,
 * the client uses the last value the server sent ({@link #setClientElapsedTicks}).
 */
public final class SeasonAccess {
    /** -1 = the client hasn't received the season yet. */
    private static volatile long clientElapsedTicks = -1;

    private SeasonAccess() {
    }

    public static Optional<Season> currentSeason(Level level) {
        return currentDate(level).map(SeasonDate::season);
    }

    public static Optional<SeasonDate> currentDate(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return Optional.of(WorldSeasons.currentDate(serverLevel));
        }
        long ticks = clientElapsedTicks;
        if (ticks < 0) {
            return Optional.empty();
        }
        return Optional.of(new SeasonCalendar(ticks, 0).date(SeasonDate.DEFAULT_YEAR_LENGTH));
    }

    /** Called when the server's season update arrives on the client. */
    public static void setClientElapsedTicks(long elapsedTicks) {
        clientElapsedTicks = elapsedTicks;
    }

    /** Called when leaving a world, so a stale season isn't shown elsewhere. */
    public static void clearClient() {
        clientElapsedTicks = -1;
    }
}
