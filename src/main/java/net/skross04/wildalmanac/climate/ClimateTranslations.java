package net.skross04.wildalmanac.climate;

import java.util.Locale;

import net.minecraft.network.chat.Component;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.ecology.climate.ClimateState;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;

/** Translation keys for climate zones and the {@code /season climate} output. */
public final class ClimateTranslations {
    /** "%1$s – %2$s °C, rainfall %3$s%%, daylight %4$s%%" ({@code %%} shows a % sign) */
    public static final String COMMAND_CLIMATE = "commands." + WildAlmanac.MODID + ".season.climate";
    public static final String COMMAND_NO_SEASONS = "commands." + WildAlmanac.MODID + ".season.climate.no_seasons";

    private ClimateTranslations() {
    }

    /** e.g. {@code climate_zone.wildalmanac.temperate} */
    public static String zoneKey(ClimateZone zone) {
        return "climate_zone." + WildAlmanac.MODID + "." + zone.id();
    }

    public static Component describe(ClimateZone zone, ClimateState climate) {
        if (!zone.hasSeasons()) {
            return Component.translatable(COMMAND_NO_SEASONS);
        }
        return Component.translatable(COMMAND_CLIMATE,
                Component.translatable(zoneKey(zone)),
                String.format(Locale.ROOT, "%.1f", climate.temperature()),
                Math.round(climate.rainfall() * 100),
                Math.round(climate.daylight() * 100));
    }
}
