package net.skross04.wildalmanac;

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

    static {
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean seasonalColors() {
        return SPEC.isLoaded() ? SEASONAL_COLORS.get() : SEASONAL_COLORS.getDefault();
    }
}
