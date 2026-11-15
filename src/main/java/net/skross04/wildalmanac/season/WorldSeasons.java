package net.skross04.wildalmanac.season;

import net.minecraft.server.level.ServerLevel;
import net.skross04.wildalmanac.ecology.season.SeasonDate;

/**
 * The bridge between Minecraft and the plain-Java season logic:
 * reads how many days have passed in the world and turns that into a {@link SeasonDate}.
 */
public final class WorldSeasons {
    /** One Minecraft day is 24000 ticks (20 minutes). */
    public static final long TICKS_PER_DAY = 24000L;

    private WorldSeasons() {
    }

    /** The current season date. Seasons are the same in every dimension, so this always uses the overworld. */
    public static SeasonDate currentDate(ServerLevel level) {
        return SeasonDate.fromDay(currentDay(level), SeasonDate.DEFAULT_YEAR_LENGTH);
    }

    /**
     * Days passed since the world was created (0 on the first day).
     * <p>
     * Since 26.1 Minecraft counts time with "world clocks": each dimension type has a default clock,
     * and sleeping moves the overworld clock forward to the morning, so sleeping keeps the days counting.
     * <p>
     * TODO (seasons step 2): replace this with Wild Almanac's own saved calendar,
     * because vanilla {@code /time set} changes this clock and must not change the season.
     */
    public static long currentDay(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        long totalTicks = overworld.dimensionType().defaultClock()
                .map(clock -> overworld.clockManager().getInstance(clock).totalTicks())
                .orElse(0L);
        return Math.floorDiv(totalTicks, TICKS_PER_DAY);
    }
}
