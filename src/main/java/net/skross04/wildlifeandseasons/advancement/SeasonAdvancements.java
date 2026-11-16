package net.skross04.wildlifeandseasons.advancement;

import java.util.OptionalDouble;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.climate.BiomeZones;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.climate.SeasonalColors;
import net.skross04.wildlifeandseasons.ecology.season.SeasonDate;
import net.skross04.wildlifeandseasons.attachment.ModAttachments;
import net.skross04.wildlifeandseasons.season.SeasonAccess;

/**
 * Grants the advancements that depend on the season or the weather (the others use vanilla triggers, see
 * ModAdvancements). Players are checked every 5 seconds; harvests and picked mushrooms when broken.
 */
@EventBusSubscriber(modid = WildlifeAndSeasons.MODID)
public final class SeasonAdvancements {
    public static final String FOLDER = "seasons/";
    /** The impossible criterion these advancements have; granted here. */
    public static final String CRITERION = "done";

    public static final String SNOWED_IN = "snowed_in";
    public static final String YEAR_IN_THE_WILD = "year_in_the_wild";
    public static final String LEAF_PEEPER = "leaf_peeper";
    public static final String HANAMI = "hanami";
    public static final String WINTER_HARVEST = "winter_harvest";
    public static final String MUSHROOM_SEASON = "mushroom_season";
    public static final String BRR = "brr";

    /** Peak autumn colours in deciduous forests (year position ≈ early to late October). */
    private static final double PEAK_AUTUMN_START = 0.56, PEAK_AUTUMN_END = 0.65;
    /** Winter starts at day 76 of 100. */
    private static final double WINTER_START = 0.75;
    /** Cherry blossom counts as "full" from this blossom strength on (0..1, see SeasonalColors.cherry). */
    private static final double FULL_BLOSSOM = 0.6;

    private SeasonAdvancements() {
    }

    public static void award(ServerPlayer player, String name) {
        AdvancementHolder advancement = player.level().getServer().getAdvancements()
                .get(Identifier.fromNamespaceAndPath(WildlifeAndSeasons.MODID, FOLDER + name));
        if (advancement != null) {
            player.getAdvancements().award(advancement, CRITERION);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 100 != 0) {
            return;
        }
        ServerLevel level = player.level();
        if (player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME)) >= SeasonDate.DEFAULT_YEAR_LENGTH * 24000L) {
            award(player, YEAR_IN_THE_WILD);
        }
        OptionalDouble year = SeasonAccess.yearPosition(level);
        BlockPos pos = player.blockPosition();
        if (year.isEmpty() || !level.canSeeSky(pos.above())) {
            return;
        }
        double p = year.getAsDouble();
        Holder<Biome> biome = level.getBiome(pos);
        ClimateZone zone = BiomeZones.of(biome.value()).zone();
        if (level.isRaining() && biome.value().getPrecipitationAt(pos, level.getSeaLevel()) == Biome.Precipitation.SNOW) {
            award(player, SNOWED_IN);
        }
        if (biome.is(BiomeTags.IS_FOREST) && (zone == ClimateZone.TEMPERATE || zone == ClimateZone.OCEAN)
                && p >= PEAK_AUTUMN_START && p <= PEAK_AUTUMN_END) {
            award(player, LEAF_PEEPER);
        }
        if (biome.is(Biomes.CHERRY_GROVE) && SeasonalColors.tint(zone, p).spring() >= FULL_BLOSSOM) {
            award(player, HANAMI);
        }
    }

    @SubscribeEvent
    public static void onBlockBroken(BreakBlockEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player) || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockState state = event.getState();
        BlockPos pos = event.getPos();
        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
            ClimateZone zone = BiomeZones.of(level.getBiome(pos).value()).zone();
            OptionalDouble year = SeasonAccess.yearPosition(level);
            boolean hasWinter = zone == ClimateZone.TEMPERATE || zone == ClimateZone.OCEAN || zone == ClimateZone.COLD;
            if (hasWinter && year.isPresent() && year.getAsDouble() >= WINTER_START) {
                award(player, WINTER_HARVEST);
            }
        }
        if ((state.is(Blocks.RED_MUSHROOM) || state.is(Blocks.BROWN_MUSHROOM) || state.is(Blocks.SHELF_MUSHROOM))
                && level.getChunkAt(pos).getData(ModAttachments.CHUNK_FLOWERS.get()).isSeasonalMushroom(pos)) {
            award(player, MUSHROOM_SEASON);
        }
    }
}
