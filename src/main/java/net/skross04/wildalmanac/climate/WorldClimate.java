package net.skross04.wildalmanac.climate;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import net.skross04.wildalmanac.ecology.climate.ClimateModel;
import net.skross04.wildalmanac.ecology.climate.ClimateState;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;
import net.skross04.wildalmanac.ecology.season.SeasonCalendar;
import net.skross04.wildalmanac.ecology.season.SeasonDate;
import net.skross04.wildalmanac.season.SeasonSavedData;
import net.skross04.wildalmanac.season.WorldSeasons;
import net.skross04.wildalmanac.util.ModTags;

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

    private static double extraOffset(ClimateZone zone, Holder<Biome> biome) {
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
