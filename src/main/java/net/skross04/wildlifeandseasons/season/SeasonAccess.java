package net.skross04.wildlifeandseasons.season;

import java.util.Optional;
import java.util.OptionalDouble;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.skross04.wildlifeandseasons.ecology.season.Season;
import net.skross04.wildlifeandseasons.ecology.season.SeasonCalendar;
import net.skross04.wildlifeandseasons.ecology.season.SeasonDate;

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

    /** How far through the year it is (0–1) on either side; empty on a client that hasn't heard the season yet. */
    public static OptionalDouble yearPosition(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return OptionalDouble.of(SeasonSavedData.get(serverLevel.getServer()).calendar()
                    .yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH));
        }
        return clientYearPosition();
    }

    /** Client side: how far through the year it is (0–1), if the server has sent the season yet. */
    public static OptionalDouble clientYearPosition() {
        long ticks = clientElapsedTicks;
        if (ticks < 0) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(new SeasonCalendar(ticks, 0).yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH));
    }

    /** Client side: the world's weather seed (which days are foggy or windy), 0 until the server sends it. */
    private static volatile long clientWeatherSeed;

    /** The world's weather seed, from the world seed (mixed so it doesn't reveal the seed). */
    public static long weatherSeed(net.minecraft.server.MinecraftServer server) {
        long h = server.overworld().getSeed() * 0x9E3779B97F4A7C15L + 0x5EA50AL;
        h ^= h >>> 31;
        h *= 0xBF58476D1CE4E5B9L;
        return h ^ (h >>> 29);
    }

    public static void setClientWeatherSeed(long seed) {
        clientWeatherSeed = seed;
    }

    /** The weather seed on either side. */
    public static long weatherSeed(Level level) {
        return level instanceof ServerLevel serverLevel ? weatherSeed(serverLevel.getServer()) : clientWeatherSeed;
    }

    /** Days since the world began (the calendar's day count) on either side; −1 if unknown on the client. */
    public static long dayIndex(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return SeasonSavedData.get(serverLevel.getServer()).calendar().dayIndex();
        }
        long ticks = clientElapsedTicks;
        return ticks < 0 ? -1 : new SeasonCalendar(ticks, 0).dayIndex();
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
