package net.skross04.wildlifeandseasons.climate;

import java.util.OptionalDouble;

import org.jspecify.annotations.Nullable;

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.skross04.wildlifeandseasons.ecology.season.SeasonCalendar;

/**
 * "Where are we in the year and in the day" for code that has no world at hand, like our Biome mixin.
 * <p>
 * The server and the client each keep their own copy, updated every tick. If a server runs in this game
 * (singleplayer or a dedicated server) its copy is used; a client connected to another server uses its own.
 * The values are replaced as a whole ({@code volatile}), so chunk-building threads always see a complete moment.
 */
public final class ClimateSnapshot {
    /** @param yearPosition 0–1 through the year; @param dayPosition 0–1 through the day (0 = sunrise) */
    public record Moment(double yearPosition, double dayPosition) {
    }

    private static volatile @Nullable Moment server;
    private static volatile @Nullable Moment client;

    private ClimateSnapshot() {
    }

    /** The current moment, or null if the season isn't known yet (then vanilla behaviour is used). */
    public static @Nullable Moment current() {
        return ServerLifecycleHooks.getCurrentServer() != null ? server : client;
    }

    public static void updateServer(double yearPosition, double dayPosition) {
        server = new Moment(yearPosition, dayPosition);
    }

    public static void updateClient(OptionalDouble yearPosition, double dayPosition) {
        client = yearPosition.isPresent() ? new Moment(yearPosition.getAsDouble(), dayPosition) : null;
    }

    public static void clearServer() {
        server = null;
    }

    public static void clearClient() {
        client = null;
    }

    /** 0–1 through the day from a level's own day clock (0 = sunrise, 0.25 = noon). */
    public static double dayPosition(Level level) {
        long ticks = level.dimensionType().defaultClock()
                .map(clock -> level.clockManager().getInstance(clock).totalTicks())
                .orElse(0L);
        return (double) Math.floorMod(ticks, SeasonCalendar.TICKS_PER_DAY) / SeasonCalendar.TICKS_PER_DAY;
    }
}
