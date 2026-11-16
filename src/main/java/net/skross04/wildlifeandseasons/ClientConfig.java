package net.skross04.wildlifeandseasons;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Settings that only affect your own screen (each player chooses for themselves).
 * Every visual effect gets its own switch, so players on weaker PCs can turn things off.
 */
public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.comment("Season visuals").push("visuals");
    }

    public static final ModConfigSpec.BooleanValue SEASONAL_COLORS = BUILDER
            .comment("Grass and leaves change colour with the seasons.",
                    "When the colours change (about once a day in spring and autumn), the chunks around you are redrawn.")
            .define("seasonalColors", true);

    public static final ModConfigSpec.BooleanValue SEASONAL_POPLARS = BUILDER
            .comment("Poplar trees and the Dappled Forest's grass follow the seasons: green in spring and summer,",
                    "red, orange or yellow in autumn, snowy in winter. Off = Mojang's look (autumn colours all year).")
            .define("seasonalPoplars", true);

    public static final ModConfigSpec.BooleanValue FOGGY_MORNINGS = BUILDER
            .comment("Foggy mornings: some mornings (most in late autumn and winter) start in thick fog that burns off",
                    "after sunrise (around noon in midwinter, sometimes not at all). Off = no extra fog.")
            .define("foggyMornings", true);

    public static final ModConfigSpec.BooleanValue WINDY_DAYS = BUILDER
            .comment("Windy days (most in autumn and winter): the trees rustle louder and more leaves fall.")
            .define("windyDays", true);

    public static final ModConfigSpec.BooleanValue SEASONAL_WATER_LILIES = BUILDER
            .comment("Lily pads follow the seasons: white and pink water lilies in summer, yellow and brown leaves",
                    "in autumn and winter, bronze new leaves in spring. Off = Mojang's green pads all year.")
            .define("seasonalWaterLilies", true);

    public static final ModConfigSpec.BooleanValue SEASONAL_FALLING_LEAVES = BUILDER
            .comment("Many falling leaves in autumn and few the rest of the year (off = vanilla amount all year).")
            .define("seasonalFallingLeaves", true);

    public static final ModConfigSpec.BooleanValue BREATH_IN_COLD = BUILDER
            .comment("Players, animals and villagers breathe out little clouds when it's cold.")
            .define("breathInCold", true);

    public static final ModConfigSpec.BooleanValue SNOW_FALLING_FROM_TREES = BUILDER
            .comment("Clumps of snow fall from snowy trees when it thaws or during storms.")
            .define("snowFallingFromTrees", true);

    public static final ModConfigSpec.BooleanValue WIND_IN_TREES = BUILDER
            .comment("Leaves rustle more on windy autumn days and in storms.")
            .define("windInTrees", true);

    static {
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean seasonalColors() {
        return SPEC.isLoaded() ? SEASONAL_COLORS.get() : SEASONAL_COLORS.getDefault();
    }

    public static boolean seasonalPoplars() {
        return SPEC.isLoaded() ? SEASONAL_POPLARS.get() : SEASONAL_POPLARS.getDefault();
    }

    public static boolean foggyMornings() {
        return SPEC.isLoaded() ? FOGGY_MORNINGS.get() : FOGGY_MORNINGS.getDefault();
    }

    public static boolean windyDays() {
        return SPEC.isLoaded() ? WINDY_DAYS.get() : WINDY_DAYS.getDefault();
    }

    public static boolean seasonalWaterLilies() {
        return SPEC.isLoaded() ? SEASONAL_WATER_LILIES.get() : SEASONAL_WATER_LILIES.getDefault();
    }

    public static boolean seasonalFallingLeaves() {
        return SPEC.isLoaded() ? SEASONAL_FALLING_LEAVES.get() : SEASONAL_FALLING_LEAVES.getDefault();
    }

    public static boolean breathInCold() {
        return SPEC.isLoaded() ? BREATH_IN_COLD.get() : BREATH_IN_COLD.getDefault();
    }

    public static boolean snowFallingFromTrees() {
        return SPEC.isLoaded() ? SNOW_FALLING_FROM_TREES.get() : SNOW_FALLING_FROM_TREES.getDefault();
    }

    public static boolean windInTrees() {
        return SPEC.isLoaded() ? WIND_IN_TREES.get() : WIND_IN_TREES.getDefault();
    }
}
