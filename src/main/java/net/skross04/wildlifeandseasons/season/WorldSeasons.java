package net.skross04.wildlifeandseasons.season;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.skross04.wildlifeandseasons.ecology.season.SeasonDate;

/**
 * The bridge between Minecraft and the plain-Java season logic.
 */
public final class WorldSeasons {
    private WorldSeasons() {
    }

    /** The current season date. Seasons are the same in every dimension. */
    public static SeasonDate currentDate(MinecraftServer server) {
        return SeasonSavedData.get(server).calendar().date(SeasonDate.DEFAULT_YEAR_LENGTH);
    }

    public static SeasonDate currentDate(ServerLevel level) {
        return currentDate(level.getServer());
    }

    /**
     * Total ticks of the overworld's day clock.
     * <p>
     * Since 26.1 Minecraft counts time with "world clocks": each dimension type has a default clock.
     * Sleeping moves the overworld clock forward to the morning; {@code /time set} changes it too,
     * which is why the season calendar only follows its natural movement (see {@code SeasonCalendar}).
     */
    public static long overworldClockTicks(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        return overworld.dimensionType().defaultClock()
                .map(clock -> overworld.clockManager().getInstance(clock).totalTicks())
                .orElse(0L);
    }
}
