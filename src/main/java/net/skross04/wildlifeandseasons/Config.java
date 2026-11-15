package net.skross04.wildlifeandseasons;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Wildlife & Seasons' settings. This is a "synced" config: the server decides, and players who join
 * get the server's values (so tooltips match what really happens).
 * File: {@code <world>/serverconfig/wildlifeandseasons-synced.toml} or {@code config/wildlifeandseasons-synced.toml}.
 */
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.comment("Crops and seasons").push("crops");
    }

    public static final ModConfigSpec.DoubleValue OUT_OF_SEASON_GROWTH = BUILDER
            .comment("How fast crops grow out of season, outside a greenhouse.",
                    "0.0 = they stop completely (default), 0.25 = a quarter of the normal speed, 1.0 = seasons don't affect crops.")
            .defineInRange("outOfSeasonGrowth", 0.0, 0.0, 1.0);

    public static final ModConfigSpec.BooleanValue BONE_MEAL_OUT_OF_SEASON = BUILDER
            .comment("Whether bone meal works on crops that are out of season (outside a greenhouse).")
            .define("boneMealOutOfSeason", false);

    public static final ModConfigSpec.IntValue GREENHOUSE_MAX_HEIGHT = BUILDER
            .comment("How many blocks above a crop the game looks for a glass roof.")
            .defineInRange("greenhouseMaxHeight", 16, 1, 64);

    static {
        BUILDER.pop();
        BUILDER.comment("Nature around you").push("ambience");
    }

    public static final ModConfigSpec.BooleanValue SEASONAL_WEATHER = BUILDER
            .comment("Rain, snow and thunder follow the seasons (showers in spring, storms in summer, long rain in autumn).",
                    "Savannas and jungles get wet and dry seasons, deserts a few rainy days in spring.")
            .define("seasonalWeather", true);

    public static final ModConfigSpec.BooleanValue SEASONAL_DAY_LENGTH = BUILDER
            .comment("Long days in summer, short days in winter. A whole day still takes 20 minutes.")
            .define("seasonalDayLength", true);

    public static final ModConfigSpec.BooleanValue LEAF_LITTER = BUILDER
            .comment("In autumn, fallen leaves slowly gather on forest floors under trees; they rot away in spring.")
            .define("leafLitter", true);

    public static final ModConfigSpec.BooleanValue CATCH_UP_CHUNKS = BUILDER
            .comment("Places nobody was near catch up with the season when you come back:",
                    "winter snow melts in summer, flowers wilt or bloom, autumn leaves have fallen.")
            .define("catchUpUnvisitedChunks", true);

    public static final ModConfigSpec.BooleanValue ICICLES = BUILDER
            .comment("Icicles grow under snowy roof edges in winter and melt in spring.")
            .define("icicles", true);

    static {
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    // Safe getters: before the config file is loaded, the defaults are used instead of crashing.

    public static double outOfSeasonGrowth() {
        return SPEC.isLoaded() ? OUT_OF_SEASON_GROWTH.get() : OUT_OF_SEASON_GROWTH.getDefault();
    }

    public static boolean boneMealOutOfSeason() {
        return SPEC.isLoaded() ? BONE_MEAL_OUT_OF_SEASON.get() : BONE_MEAL_OUT_OF_SEASON.getDefault();
    }

    public static int greenhouseMaxHeight() {
        return SPEC.isLoaded() ? GREENHOUSE_MAX_HEIGHT.get() : GREENHOUSE_MAX_HEIGHT.getDefault();
    }

    public static boolean leafLitter() {
        return SPEC.isLoaded() ? LEAF_LITTER.get() : LEAF_LITTER.getDefault();
    }

    public static boolean seasonalWeather() {
        return SPEC.isLoaded() ? SEASONAL_WEATHER.get() : SEASONAL_WEATHER.getDefault();
    }

    public static boolean seasonalDayLength() {
        return SPEC.isLoaded() ? SEASONAL_DAY_LENGTH.get() : SEASONAL_DAY_LENGTH.getDefault();
    }

    public static boolean icicles() {
        return SPEC.isLoaded() ? ICICLES.get() : ICICLES.getDefault();
    }

    public static boolean catchUpChunks() {
        return SPEC.isLoaded() ? CATCH_UP_CHUNKS.get() : CATCH_UP_CHUNKS.getDefault();
    }
}
