package net.skross04.wildalmanac.nature;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.skross04.wildalmanac.Config;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.attachment.ModAttachments;
import net.skross04.wildalmanac.climate.SeasonalThaw;
import net.skross04.wildalmanac.climate.WorldClimate;
import net.skross04.wildalmanac.ecology.climate.SnowAndIce;
import net.skross04.wildalmanac.season.SeasonSavedData;

/**
 * Snow, flowers and leaf litter only change in chunks near a player (Minecraft only "random ticks" those).
 * So a place you left in winter would still be white when you come back in summer.
 * <p>
 * Fix: every chunk remembers the calendar day it was last up to date ({@link ModAttachments#SEASON_DAY}).
 * When a chunk loads and that day is in the past, it goes in a queue; a few chunks per tick are then
 * brought up to the season, column by column:
 * <ul>
 *     <li>warm now: snow layers and frozen water open to the sky melt;</li>
 *     <li>wild flowers wilt or bloom again, planted ones close or open;</li>
 *     <li>forest floors get the leaf litter they'd have by now (or lose it in spring);</li>
 *     <li>cold and snowy now: snow lies on the ground, still water is frozen.</li>
 * </ul>
 * Brand-new chunks don't need it (world generation already follows the season).
 */
@EventBusSubscriber(modid = WildAlmanac.MODID)
public final class ChunkCatchUp {
    /** Chunks processed per tick per world, so walking into an old area doesn't cause a lag spike. */
    private static final int CHUNKS_PER_TICK = 4;
    /** A chunk that missed more than a year catches up as if it missed one year. */
    private static final long MAX_MISSED_DAYS = 100;
    /** °C (today's average): water only freezes over in a real frost, not on a borderline day. */
    private static final double FREEZE_TEMPERATURE = -2.0;

    private static final Map<ResourceKey<Level>, Queue<ChunkPos>> QUEUES = new HashMap<>();

    private ChunkCatchUp() {
    }

    private static long today(ServerLevel level) {
        return SeasonSavedData.get(level.getServer()).calendar().dayIndex();
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }
        if (event.isNewChunk()) {
            chunk.setData(ModAttachments.SEASON_DAY.get(), today(level)); // generated for today's season
            return;
        }
        if (chunk.getData(ModAttachments.SEASON_DAY.get()) != today(level)) {
            // Change blocks later, in the world tick: changing them while the chunk is still loading isn't safe.
            synchronized (QUEUES) {
                QUEUES.computeIfAbsent(level.dimension(), key -> new ArrayDeque<>()).add(chunk.getPos());
            }
        }
    }

    /** While a chunk is loaded near players it stays up to date by itself, so remember today when it unloads. */
    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level && event.getChunk() instanceof LevelChunk chunk) {
            long today = today(level);
            Long known = chunk.getExistingDataOrNull(ModAttachments.SEASON_DAY.get());
            if (known != null && known != -1L && known != today) {
                chunk.setData(ModAttachments.SEASON_DAY.get(), today);
                chunk.markUnsaved();
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        Queue<ChunkPos> queue;
        synchronized (QUEUES) {
            queue = QUEUES.get(level.dimension());
        }
        if (queue == null) {
            return;
        }
        for (int i = 0; i < CHUNKS_PER_TICK; i++) {
            ChunkPos pos;
            synchronized (QUEUES) {
                pos = queue.poll();
            }
            if (pos == null) {
                return;
            }
            LevelChunk chunk = level.getChunkSource().getChunkNow(pos.x(), pos.z());
            if (chunk != null) {
                catchUp(level, chunk);
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        synchronized (QUEUES) {
            QUEUES.clear();
        }
    }

    private static void catchUp(ServerLevel level, LevelChunk chunk) {
        long today = today(level);
        long known = chunk.getData(ModAttachments.SEASON_DAY.get());
        chunk.setData(ModAttachments.SEASON_DAY.get(), today);
        chunk.markUnsaved();
        if (!Config.catchUpChunks()) {
            return;
        }
        // Unknown (a chunk from before this feature) or set back with /season set: catch up fully.
        long missed = known < 0 || known > today ? MAX_MISSED_DAYS : Math.min(MAX_MISSED_DAYS, today - known);
        RandomSource random = level.getRandom();
        ChunkPos chunkPos = chunk.getPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                catchUpColumn(level, chunkPos.getBlockX(x), chunkPos.getBlockZ(z), missed, random);
            }
        }
    }

    private static void catchUpColumn(ServerLevel level, int x, int z, long missed, RandomSource random) {
        // Top of the column (leaves count) and the ground under any trees.
        BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(x, 0, z));
        BlockPos floor = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
        Holder<Biome> biome = level.getBiome(top);
        double dailyMean = WorldClimate.dailyMeanAt(level, top);
        if (Double.isNaN(dailyMean)) {
            return; // no seasons here (Nether, End)
        }
        boolean warm = dailyMean > SnowAndIce.THAW_TEMPERATURE;
        boolean snowy = dailyMean <= SnowAndIce.SNOW_TEMPERATURE && biome.value().hasPrecipitation();

        if (warm) {
            meltSnow(level, top);
            meltSnow(level, floor);
            BlockPos surface = top.below();
            if (level.getBlockState(surface).is(Blocks.ICE) && level.getFluidState(surface.below()).isSource()) {
                level.setBlockAndUpdate(surface, Blocks.WATER.defaultBlockState()); // a frozen lake thawed
            }
        }

        SeasonalFlowers.catchUp(level, floor.below(), random);
        LeafLitter.catchUp(level, floor.below(), random, missed, snowy);

        if (snowy) {
            BlockPos surface = top.below();
            if (dailyMean <= FREEZE_TEMPERATURE && level.getBlockState(surface).is(Blocks.WATER)
                    && level.getFluidState(surface).isSource() && level.getBrightness(LightLayer.BLOCK, surface) < 10) {
                level.setBlockAndUpdate(surface, Blocks.ICE.defaultBlockState());
            } else if (level.getBlockState(top).isAir() && level.getBrightness(LightLayer.BLOCK, top) < 10
                    && Blocks.SNOW.defaultBlockState().canSurvive(level, top)) {
                level.setBlockAndUpdate(top, Blocks.SNOW.defaultBlockState());
            }
        }
    }

    /** Removes a snow layer at this spot or just below it (a thick layer counts as solid for the heightmap). */
    private static void meltSnow(ServerLevel level, BlockPos pos) {
        for (BlockPos spot : new BlockPos[]{pos, pos.below()}) {
            BlockState state = level.getBlockState(spot);
            if (state.is(Blocks.SNOW) && SeasonalThaw.isOpenToSkyOrCanopy(level, spot)) {
                level.removeBlock(spot, false);
            }
        }
    }
}
