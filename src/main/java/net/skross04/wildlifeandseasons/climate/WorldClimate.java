package net.skross04.wildlifeandseasons.climate;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.skross04.wildlifeandseasons.Config;
import net.neoforged.neoforge.common.Tags;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateModel;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateState;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.climate.SeasonalWeather;
import net.skross04.wildlifeandseasons.ecology.season.SeasonCalendar;
import net.skross04.wildlifeandseasons.ecology.season.SeasonDate;
import net.skross04.wildlifeandseasons.season.SeasonSavedData;
import net.skross04.wildlifeandseasons.season.WorldSeasons;
import net.skross04.wildlifeandseasons.util.ModTags;

/**
 * The bridge between Minecraft biomes and the plain-Java climate model.
 */
public final class WorldClimate {
    /**
     * Order in which the zone tags are checked; the first match wins. The temperate tag is empty by default,
     * so it's checked early: adding a biome to it in a datapack always works.
     */
    private static final List<ClimateZone> TAG_CHECK_ORDER = List.of(
            ClimateZone.NO_SEASONS, ClimateZone.TEMPERATE, ClimateZone.OCEAN, ClimateZone.ARID,
            ClimateZone.SAVANNA, ClimateZone.TROPICAL, ClimateZone.COLD);

    /** Extra °C for warm or icy waters, since vanilla gives most oceans the same temperature. */
    private static final double OCEAN_TAG_OFFSET = 5.0;

    private WorldClimate() {
    }

    /** Which climate zone a biome belongs to: zone tags first, otherwise the biome's vanilla climate values. */
    public static ClimateZone zoneOf(Holder<Biome> biome) {
        for (ClimateZone zone : TAG_CHECK_ORDER) {
            if (biome.is(ModTags.Biomes.climateZone(zone))) {
                return zone;
            }
        }
        Biome.ClimateSettings climate = biome.value().getModifiedClimateSettings();
        return ClimateZone.fromVanillaClimate(climate.temperature(), climate.downfall());
    }

    /** The climate at a position right now. */
    public static ClimateState climateAt(ServerLevel level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        ClimateZone zone = zoneOf(biome);
        MinecraftServer server = level.getServer();

        double yearPosition = SeasonSavedData.get(server).calendar().yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH);
        double dayPosition = (double) Math.floorMod(WorldSeasons.overworldClockTicks(server), SeasonCalendar.TICKS_PER_DAY)
                / SeasonCalendar.TICKS_PER_DAY;

        return ClimateModel.sample(zone, biome.value().getModifiedClimateSettings().temperature(),
                extraOffset(zone, biome), pos.getY(), yearPosition, dayPosition);
    }

    /**
     * The temperature right now at a position, for code without a world (the Biome mixin).
     * NaN when it isn't known (season not loaded yet, or a place without seasons): then vanilla decides.
     */
    public static double temperatureNow(Biome biome, BlockPos pos) {
        ClimateSnapshot.Moment moment = ClimateSnapshot.current();
        if (moment == null) {
            return Double.NaN;
        }
        BiomeZones.BiomeClimate climate = BiomeZones.of(biome);
        if (!climate.zone().hasSeasons()) {
            return Double.NaN;
        }
        return ClimateModel.sample(climate.zone(), climate.biomeTemperature(), climate.extraOffset(), pos.getY(),
                moment.yearPosition(), moment.dayPosition()).temperature();
    }

    /**
     * Wet and dry seasons for code without a world (the Biome mixin): whether this biome can have rain now,
     * or null where vanilla decides (also when the season isn't known yet).
     */
    public static Boolean wetSeasonRain(Biome biome) {
        ClimateSnapshot.Moment moment = ClimateSnapshot.current();
        if (moment == null || !Config.seasonalWeather()) {
            return null;
        }
        BiomeZones.BiomeClimate climate = BiomeZones.of(biome);
        double rainfall = ClimateModel.sample(climate.zone(), climate.biomeTemperature(), climate.extraOffset(), 64,
                moment.yearPosition(), moment.dayPosition()).rainfall();
        return SeasonalWeather.hasRain(climate.zone(), rainfall);
    }

    /** Today's average temperature for code without a world (client effects). NaN when unknown or without seasons. */
    public static double dailyMeanNow(Biome biome, BlockPos pos) {
        ClimateSnapshot.Moment moment = ClimateSnapshot.current();
        if (moment == null) {
            return Double.NaN;
        }
        BiomeZones.BiomeClimate climate = BiomeZones.of(biome);
        if (!climate.zone().hasSeasons()) {
            return Double.NaN;
        }
        return ClimateModel.dailyMeanTemperature(climate.zone(), climate.biomeTemperature(), climate.extraOffset(),
                pos.getY(), moment.yearPosition());
    }

    /**
     * Today's average temperature for a biome you already have (server side), or NaN where there are no seasons.
     * For chunks that are still loading, where the world can't look up the biome yet.
     */
    public static double dailyMeanAt(ServerLevel level, Holder<Biome> biome, BlockPos pos) {
        BiomeZones.BiomeClimate climate = BiomeZones.of(biome.value());
        if (!climate.zone().hasSeasons()) {
            return Double.NaN;
        }
        double yearPosition = SeasonSavedData.get(level.getServer()).calendar().yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH);
        return ClimateModel.dailyMeanTemperature(climate.zone(), climate.biomeTemperature(), climate.extraOffset(),
                pos.getY(), yearPosition);
    }

    /** The average temperature of today at a position (server side), or NaN where there are no seasons. */
    public static double dailyMeanAt(ServerLevel level, BlockPos pos) {
        BiomeZones.BiomeClimate climate = BiomeZones.of(level.getBiome(pos).value());
        if (!climate.zone().hasSeasons()) {
            return Double.NaN;
        }
        double yearPosition = SeasonSavedData.get(level.getServer()).calendar().yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH);
        return ClimateModel.dailyMeanTemperature(climate.zone(), climate.biomeTemperature(), climate.extraOffset(),
                pos.getY(), yearPosition);
    }

    public static double extraOffset(ClimateZone zone, Holder<Biome> biome) {
        if (zone == ClimateZone.OCEAN) {
            if (biome.is(Tags.Biomes.IS_COLD_OVERWORLD)) {
                return -OCEAN_TAG_OFFSET;
            }
            if (biome.is(Tags.Biomes.IS_HOT_OVERWORLD)) {
                return OCEAN_TAG_OFFSET;
            }
        }
        return 0.0;
    }
}
