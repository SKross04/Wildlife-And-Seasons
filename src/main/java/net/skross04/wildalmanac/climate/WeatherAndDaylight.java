package net.skross04.wildalmanac.climate;

import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.WeatherData;
import net.skross04.wildalmanac.Config;
import net.skross04.wildalmanac.ecology.climate.ClimateModel;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;
import net.skross04.wildalmanac.ecology.climate.SeasonalWeather;
import net.skross04.wildalmanac.ecology.season.Season;
import net.skross04.wildalmanac.ecology.season.SeasonCalendar;
import net.skross04.wildalmanac.ecology.season.SeasonDate;
import net.skross04.wildalmanac.season.SeasonSavedData;

/**
 * Runs every server tick:
 * <ul>
 *     <li><b>Weather:</b> whenever vanilla picks a new random rain or thunder timer, replace it with one that fits
 *     the season ({@link SeasonalWeather}). {@code /weather} commands are left alone.</li>
 *     <li><b>Day length:</b> long summer days and short winter days, by running the day clock a bit slower or
 *     faster. A whole day still takes 20 minutes, so farms and sleeping work as usual.</li>
 * </ul>
 * The weather is the same everywhere in the world (vanilla has one weather), so it follows the temperate seasons.
 */
public final class WeatherAndDaylight {
    /** Daylight is for the whole world too (one sky), so it uses the temperate zone. */
    private static final double DAYLIGHT_REFERENCE_BIOME_TEMPERATURE = 0.8;

    private static int lastRainTime = -1;
    private static int lastThunderTime = -1;
    private static float lastClockRate = 1.0F;

    private WeatherAndDaylight() {
    }

    public static void tick(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        SeasonCalendar calendar = SeasonSavedData.get(server).calendar();
        tickWeather(overworld, calendar.date(SeasonDate.DEFAULT_YEAR_LENGTH).season());
        tickDayLength(overworld, calendar.yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH));
    }

    private static void tickWeather(ServerLevel overworld, Season season) {
        WeatherData weather = overworld.getWeatherData();
        boolean enabled = Config.seasonalWeather() && overworld.getGameRules().get(GameRules.ADVANCE_WEATHER);
        // Vanilla counts a timer down to 0, flips the weather, and on the next tick rolls a new random timer.
        // That "0 → new value" step is what we replace. (/weather sets the timer directly, never from 0.)
        if (enabled && weather.getClearWeatherTime() == 0) {
            RandomSource random = overworld.getRandom();
            if (lastRainTime == 0 && weather.getRainTime() > 0) {
                weather.setRainTime(weather.isRaining()
                        ? SeasonalWeather.rainDuration(season, random.nextDouble())
                        : SeasonalWeather.dryDuration(season, random.nextDouble()));
            }
            if (lastThunderTime == 0 && weather.getThunderTime() > 0) {
                weather.setThunderTime(weather.isThundering()
                        ? SeasonalWeather.thunderDuration(season, random.nextDouble())
                        : SeasonalWeather.calmDuration(season, random.nextDouble()));
            }
        }
        lastRainTime = weather.getRainTime();
        lastThunderTime = weather.getThunderTime();
    }

    private static void tickDayLength(ServerLevel overworld, double yearPosition) {
        Holder<WorldClock> clock = overworld.dimensionType().defaultClock().orElse(null);
        if (clock == null) {
            return;
        }
        float rate = 1.0F;
        if (Config.seasonalDayLength()) {
            double daylight = ClimateModel.sample(ClimateZone.TEMPERATE, DAYLIGHT_REFERENCE_BIOME_TEMPERATURE, 0, 64,
                    yearPosition, 0).daylight();
            boolean daytime = ClimateSnapshot.dayPosition(overworld) < 0.5; // 0 = sunrise, 0.5 = sunset
            rate = SeasonalWeather.clockRate(daylight, daytime);
        }
        if (Math.abs(rate - lastClockRate) > 0.001F) {
            overworld.clockManager().setRate(clock, rate);
            lastClockRate = rate;
        }
    }

    /** Forget the remembered values when the server stops (another world may be loaded next). */
    public static void reset() {
        lastRainTime = -1;
        lastThunderTime = -1;
        lastClockRate = 1.0F;
    }
}
