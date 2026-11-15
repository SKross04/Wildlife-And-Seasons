package net.skross04.wildalmanac;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Wild Almanac's settings. This is a "synced" config: the server decides, and players who join
 * get the server's values (so tooltips match what really happens).
 * File: {@code <world>/serverconfig/wildalmanac-synced.toml} or {@code config/wildalmanac-synced.toml}.
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
}
