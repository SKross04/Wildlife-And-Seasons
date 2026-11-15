package net.skross04.wildlifeandseasons.climate;

import java.util.Locale;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateState;
import net.skross04.wildlifeandseasons.ecology.climate.InstrumentRules;

/** Readings for the thermometer and the rain gauge (server side). */
public final class Instruments {
    /** "Thermometer: %s °C" */
    public static final String THERMOMETER_READING = "message." + WildlifeAndSeasons.MODID + ".thermometer";
    /** "Right-click to read the temperature here" */
    public static final String THERMOMETER_TOOLTIP = "item." + WildlifeAndSeasons.MODID + ".thermometer.tooltip";
    /** "Rain gauge: %1$s%% full · %2$s%% rainfall this season" */
    public static final String RAIN_GAUGE_READING = "message." + WildlifeAndSeasons.MODID + ".rain_gauge";

    private Instruments() {
    }

    public static Component thermometerReading(ServerLevel level, BlockPos pos) {
        double temperature = WorldClimate.climateAt(level, pos).temperature();
        return Component.translatable(THERMOMETER_READING, String.format(Locale.ROOT, "%.1f", temperature));
    }

    public static Component rainGaugeReading(ServerLevel level, BlockPos pos, int fill) {
        ClimateState climate = WorldClimate.climateAt(level, pos);
        return Component.translatable(RAIN_GAUGE_READING, InstrumentRules.fillPercent(fill),
                Math.round(climate.rainfall() * 100));
    }

    /** The gauge's next fill level: rain falling on it fills it, warm dry weather empties it slowly. */
    public static int nextGaugeFill(ServerLevel level, BlockPos pos, int fill, RandomSource random) {
        if (level.isRainingAt(pos.above())) {
            return Math.min(InstrumentRules.GAUGE_MAX, fill + InstrumentRules.rainAdded(level.isThundering()));
        }
        if (fill > 0 && !level.isRaining()
                && random.nextDouble() < InstrumentRules.evaporationChance(WorldClimate.climateAt(level, pos).temperature())) {
            return fill - 1;
        }
        return fill;
    }
}
