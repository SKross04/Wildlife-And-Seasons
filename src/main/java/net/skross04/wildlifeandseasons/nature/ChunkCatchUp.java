package net.skross04.wildlifeandseasons.nature;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LeafLitterBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SegmentableBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.skross04.wildlifeandseasons.Config;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.attachment.ModAttachments;
import net.skross04.wildlifeandseasons.block.ModBlocks;
import net.skross04.wildlifeandseasons.block.custom.IcicleBlock;
import net.skross04.wildlifeandseasons.block.entity.DormantFlowerBlockEntity;
import net.skross04.wildlifeandseasons.climate.BiomeZones;
import net.skross04.wildlifeandseasons.climate.WorldClimate;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.climate.IcicleRules;
import net.skross04.wildlifeandseasons.ecology.climate.SeasonalColors;
import net.skross04.wildlifeandseasons.ecology.climate.SnowAndIce;
import net.skross04.wildlifeandseasons.ecology.farming.CropSeasonRules;
import net.skross04.wildlifeandseasons.ecology.season.Season;
import net.skross04.wildlifeandseasons.ecology.season.SeasonDate;
import net.skross04.wildlifeandseasons.ecology.vegetation.FlowerRules;
import net.skross04.wildlifeandseasons.ecology.vegetation.LeafLitterRules;
import net.skross04.wildlifeandseasons.farming.CropSeasons;
import net.skross04.wildlifeandseasons.season.SeasonSavedData;
import net.skross04.wildlifeandseasons.util.ModTags;

/**
 * Snow, flowers, leaf litter, icicles and crops only change in chunks close to a player (Minecraft only
 * "random ticks" those). So a place you left in winter would still be white when you come back in summer.
 * <p>
 * Every chunk remembers the calendar day it last kept up with the season ({@link ModAttachments#SEASON_DAY}).
 * That day only moves forward while the chunk really gets random ticks (see {@code ServerLevelMixin}), so chunks
 * at the edge of your view, which are loaded but not ticked, don't count as up to date.
 * <ul>
 *     <li><b>Loading</b> a chunk that is behind: it catches up during loading, before it's sent to anyone,
 *     so you never see the old season.</li>
 *     <li><b>Ticked</b> chunks that are more than a day behind (they sat at the edge of view, or {@code /season set}
 *     jumped ahead): they're queued and catch up a few per tick.</li>
 * </ul>
 * Catching up, column by column: warm → snow and lake ice melt, natural icicles melt; flowers wilt or bloom;
 * forest floors get the leaf litter they'd have by now; crops, stems (with their melons and pumpkins), sweet berries
 * and cocoa grow for the days they were in season;
 * cold and snowy → snow lies on the ground, still water freezes, icicles hang under snowy roof edges.
 * Brand-new chunks don't need it: world generation already follows the season.
 */
@EventBusSubscriber(modid = WildlifeAndSeasons.MODID)
public final class ChunkCatchUp {
    /** Queued chunks processed per tick per world, so a big jump doesn't cause a lag spike. */
    private static final int CHUNKS_PER_TICK = 4;
    /** A chunk that missed more than a year catches up as if it missed one year. */
    private static final long MAX_MISSED_DAYS = SeasonDate.DEFAULT_YEAR_LENGTH;
    /** °C (today's average): water only freezes over in a real frost, not on a borderline day. */
    private static final double FREEZE_TEMPERATURE = -2.0;
    /**
     * Flags for changing blocks in a chunk that is still loading. 2 = the normal "tell clients" flag;
     * 256 = skip the old block entity's side effects and 512 = skip {@code onPlace}: both would ask the world
     * about this chunk, which isn't finished yet (that would wait for itself and freeze the server).
     */
    private static final int LOADING_FLAGS = Block.UPDATE_CLIENTS | 256 | 512;

    private static final Map<ResourceKey<Level>, Queue<Pending>> QUEUES = new HashMap<>();

    private record Pending(ChunkPos pos, long missedDays) {
    }

    private ChunkCatchUp() {
    }

    private static long today(ServerLevel level) {
        return SeasonSavedData.get(level.getServer()).calendar().dayIndex();
    }

    /** Days a chunk missed, from its remembered day (-1 = never known, or set back with /season set: a full year). */
    private static long missedDays(long known, long today) {
        return known < 0 || known > today ? MAX_MISSED_DAYS : Math.min(MAX_MISSED_DAYS, today - known);
    }

    // ---------------------------------------------------------------------------------------------------------
    // When to catch up
    // ---------------------------------------------------------------------------------------------------------

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }
        long today = today(level);
        Long known = chunk.getExistingDataOrNull(ModAttachments.SEASON_DAY.get());
        if (event.isNewChunk() || (known != null && known == today)) {
            chunk.setData(ModAttachments.SEASON_DAY.get(), today); // generated for today's season, or up to date
            return;
        }
        chunk.setData(ModAttachments.SEASON_DAY.get(), today);
        chunk.markUnsaved();
        if (Config.catchUpChunks()) {
            // Runs on the server thread while the chunk is being finished, before players get it.
            catchUp(level, chunk, missedDays(known == null ? -1 : known, today), new LoadingChunk(chunk));
        }
    }

    /** Called for every chunk that gets random ticks (see {@code ServerLevelMixin}). */
    public static void onChunkTicked(ServerLevel level, LevelChunk chunk) {
        long today = cachedToday(level);
        Long known = chunk.getExistingDataOrNull(ModAttachments.SEASON_DAY.get());
        if (known != null && known == today) {
            return; // the usual case: one quick lookup
        }
        chunk.setData(ModAttachments.SEASON_DAY.get(), today);
        chunk.markUnsaved();
        if (known != null && known == today - 1) {
            return; // a new day while it was being ticked: random ticks kept it up to date
        }
        if (Config.catchUpChunks()) {
            synchronized (QUEUES) {
                QUEUES.computeIfAbsent(level.dimension(), key -> new ArrayDeque<>())
                        .add(new Pending(chunk.getPos(), missedDays(known == null ? -1 : known, today)));
            }
        }
    }

    private static ServerLevel cachedLevel;
    private static long cachedTime = Long.MIN_VALUE;
    private static long cachedDay;

    /** {@code onChunkTicked} runs for hundreds of chunks every tick: look the day up once per tick. */
    private static long cachedToday(ServerLevel level) {
        if (level != cachedLevel || level.getGameTime() != cachedTime) {
            cachedLevel = level;
            cachedTime = level.getGameTime();
            cachedDay = today(level);
        }
        return cachedDay;
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        for (int i = 0; i < CHUNKS_PER_TICK; i++) {
            Pending pending;
            synchronized (QUEUES) {
                Queue<Pending> queue = QUEUES.get(level.dimension());
                pending = queue == null ? null : queue.poll();
            }
            if (pending == null) {
                return;
            }
            LevelChunk chunk = level.getChunkSource().getChunkNow(pending.pos().x(), pending.pos().z());
            if (chunk != null) {
                catchUp(level, chunk, pending.missedDays(), new LoadedChunk(level, chunk));
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        synchronized (QUEUES) {
            QUEUES.clear();
        }
        cachedLevel = null;
    }

    // ---------------------------------------------------------------------------------------------------------
    // How blocks are changed: in a loading chunk only through the chunk itself, in a loaded one through the world
    // ---------------------------------------------------------------------------------------------------------

    private interface ChunkBlocks {
        LevelChunk chunk();

        void set(BlockPos pos, BlockState state);

        default BlockState get(BlockPos pos) {
            return chunk().getBlockState(pos);
        }
    }

    private record LoadingChunk(LevelChunk chunk) implements ChunkBlocks {
        @Override
        public void set(BlockPos pos, BlockState state) {
            chunk.setBlockState(pos, state, LOADING_FLAGS);
        }
    }

    /** Already loaded (and maybe in view): changes go through the world so players see them. */
    private record LoadedChunk(ServerLevel level, LevelChunk chunk) implements ChunkBlocks {
        @Override
        public void set(BlockPos pos, BlockState state) {
            level.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        }
    }

    // ---------------------------------------------------------------------------------------------------------
    // Catching up
    // ---------------------------------------------------------------------------------------------------------

    private static void catchUp(ServerLevel level, LevelChunk chunk, long missed, ChunkBlocks blocks) {
        if (missed <= 0) {
            return;
        }
        RandomSource random = level.getRandom();
        ChunkPos chunkPos = chunk.getPos();
        double yearPosition = SeasonSavedData.get(level.getServer()).calendar().yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                catchUpColumn(level, blocks, chunkPos.getBlockX(x), chunkPos.getBlockZ(z), missed, yearPosition, random);
            }
        }
        catchUpFlowersThatWilted(level, blocks, yearPosition);
        catchUpIciclesAndCrops(level, blocks, missed, random);
        chunk.markUnsaved();
    }

    private static Holder<Biome> biomeAt(LevelChunk chunk, BlockPos pos) {
        return chunk.getNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()));
    }

    private static void catchUpColumn(ServerLevel level, ChunkBlocks blocks, int x, int z, long missed,
                                      double yearPosition, RandomSource random) {
        LevelChunk chunk = blocks.chunk();
        // The top solid block of the column (leaves and water count) and the ground under any trees.
        BlockPos top = new BlockPos(x, chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), z);
        BlockPos ground = new BlockPos(x, chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
        if (top.getY() <= chunk.getMinY()) {
            return; // an empty column (the void)
        }
        Holder<Biome> biome = biomeAt(chunk, top);
        double dailyMean = WorldClimate.dailyMeanAt(level, biome, top);
        if (Double.isNaN(dailyMean)) {
            return; // no seasons here (Nether, End)
        }
        boolean warm = dailyMean > SnowAndIce.THAW_TEMPERATURE;
        boolean snowy = dailyMean <= SnowAndIce.SNOW_TEMPERATURE && biome.value().hasPrecipitation();

        if (warm) {
            meltSnow(blocks, top, top);
            meltSnow(blocks, ground, top);
            BlockState surface = blocks.get(top);
            if (surface.is(Blocks.ICE) && chunk.getFluidState(top.below()).isSource()) {
                blocks.set(top, Blocks.WATER.defaultBlockState()); // a frozen lake thawed
            }
        }

        ClimateZone zone = BiomeZones.of(biome.value()).zone();
        settleFlower(blocks, ground.above(), zone, yearPosition);
        settleForestFloor(blocks, ground, top, biome, zone, yearPosition, missed, snowy, random);

        if (snowy) {
            BlockState surface = blocks.get(top);
            if (dailyMean <= FREEZE_TEMPERATURE && surface.is(Blocks.WATER) && chunk.getFluidState(top).isSource()) {
                blocks.set(top, Blocks.ICE.defaultBlockState());
            } else if (snowCanLieOn(chunk, top, surface)) {
                BlockState onTop = blocks.get(top.above());
                if (SnowCover.canBury(onTop)) {
                    buryPlant(blocks, top.above(), onTop); // snow covers the grass instead of leaving a hole
                } else if (onTop.isAir()) {
                    placeSnow(blocks, top.above());
                }
            }
            if (IcicleRules.canGrow(dailyMean) && Config.icicles()) {
                hangIcicles(blocks, top, missed, random);
            }
        }
    }

    // --- Snow -------------------------------------------------------------------------------------------------

    /** Removes a snow layer just above {@code solid} or the thick snow layer that is {@code solid}, if open to the sky. */
    private static void meltSnow(ChunkBlocks blocks, BlockPos solid, BlockPos top) {
        for (BlockPos spot : new BlockPos[]{solid.above(), solid}) {
            if (blocks.get(spot).is(Blocks.SNOW) && openToSkyOrCanopy(blocks, spot, top)) {
                blocks.set(spot, Blocks.AIR.defaultBlockState());
                setSnowy(blocks, spot.below(), false);
                uncoverPlant(blocks, spot);
                return;
            }
        }
    }

    /** Covers grass with snow and remembers it (the top half of tall grass goes too). */
    private static void buryPlant(ChunkBlocks blocks, BlockPos pos, BlockState plant) {
        if (isTall(plant) && blocks.get(pos.above()).is(plant.getBlock())) {
            blocks.set(pos.above(), Blocks.AIR.defaultBlockState());
        }
        blocks.chunk().getData(ModAttachments.CHUNK_FLOWERS.get()).addWilted(pos, plant);
        placeSnow(blocks, pos);
    }

    /** The grass that was under this snow grows back. */
    private static void uncoverPlant(ChunkBlocks blocks, BlockPos pos) {
        ChunkFlowers memory = blocks.chunk().getExistingDataOrNull(ModAttachments.CHUNK_FLOWERS.get());
        BlockState plant = memory == null ? null : memory.peekWilted(pos);
        if (plant != null && SnowCover.canBury(plant) && blocks.get(pos.below()).is(BlockTags.DIRT)
                && (!isTall(plant) || blocks.get(pos.above()).isAir())) {
            placeFlower(blocks, pos, plant);
            memory.takeWilted(pos);
        }
    }

    /** Nothing above but sky, or only tree leaves: a real roof (igloo, house) keeps its snow, like the normal thaw. */
    private static boolean openToSkyOrCanopy(ChunkBlocks blocks, BlockPos spot, BlockPos top) {
        return top.getY() <= spot.getY() || blocks.get(top).getBlock() instanceof LeavesBlock;
    }

    /** Vanilla's snow rule: not on ice or barriers, always on honey, soul sand and mud, otherwise on a full top face. */
    private static boolean snowCanLieOn(LevelChunk chunk, BlockPos pos, BlockState below) {
        if (below.is(Blocks.ICE) || below.is(Blocks.PACKED_ICE) || below.is(Blocks.BARRIER)) {
            return false;
        }
        return below.is(Blocks.HONEY_BLOCK) || below.is(Blocks.SOUL_SAND) || below.is(Blocks.MUD)
                || Block.isFaceFull(below.getCollisionShape(chunk, pos), Direction.UP);
    }

    private static void placeSnow(ChunkBlocks blocks, BlockPos pos) {
        blocks.set(pos, Blocks.SNOW.defaultBlockState());
        setSnowy(blocks, pos.below(), true);
    }

    /** Grass, podzol and mycelium look snowy from the side when snow lies on them. */
    private static void setSnowy(ChunkBlocks blocks, BlockPos pos, boolean snowy) {
        BlockState state = blocks.get(pos);
        if (state.hasProperty(BlockStateProperties.SNOWY) && state.getValue(BlockStateProperties.SNOWY) != snowy) {
            blocks.set(pos, state.setValue(BlockStateProperties.SNOWY, snowy));
        }
    }

    // --- Flowers ----------------------------------------------------------------------------------------------

    private static boolean isTall(BlockState flower) {
        return flower.getBlock() instanceof DoublePlantBlock;
    }

    private static boolean bloomsNow(BlockState flower, ClimateZone zone, double yearPosition) {
        return isTall(flower) ? FlowerRules.isTallBloomTime(zone, yearPosition) : FlowerRules.isBloomTime(zone, yearPosition);
    }

    /** The flower standing on the ground of this column: wilt it (wild) or close it (planted) when its time is over. */
    private static void settleFlower(ChunkBlocks blocks, BlockPos pos, ClimateZone zone, double yearPosition) {
        BlockState state = blocks.get(pos);
        LevelChunk chunk = blocks.chunk();
        if (SeasonalFlowers.isSeasonalFlower(state) || SeasonalFlowers.isSeasonalTallFlower(state)) {
            if (bloomsNow(state, zone, yearPosition)) {
                return;
            }
            ChunkFlowers memory = chunk.getData(ModAttachments.CHUNK_FLOWERS.get());
            if (isTall(state) && blocks.get(pos.above()).is(state.getBlock())) {
                blocks.set(pos.above(), Blocks.AIR.defaultBlockState());
            }
            if (memory.isPlanted(pos)) {
                blocks.set(pos, ModBlocks.DORMANT_FLOWER.get().defaultBlockState());
                if (chunk.getBlockEntity(pos) instanceof DormantFlowerBlockEntity bud) {
                    bud.setFlowerQuietly(state);
                }
            } else {
                memory.addWilted(pos, state);
                blocks.set(pos, Blocks.AIR.defaultBlockState());
            }
        } else if (state.is(ModBlocks.DORMANT_FLOWER.get())
                && chunk.getBlockEntity(pos) instanceof DormantFlowerBlockEntity bud
                && bloomsNow(bud.getFlower(), zone, yearPosition)) {
            BlockState flower = bud.getFlower();
            if (!isTall(flower) || blocks.get(pos.above()).isAir()) {
                placeFlower(blocks, pos, flower);
            }
        }
    }

    /** Wild flowers that wilted here come back when it's their time again, grass that was snowed under once it thaws. */
    private static void catchUpFlowersThatWilted(ServerLevel level, ChunkBlocks blocks, double yearPosition) {
        LevelChunk chunk = blocks.chunk();
        ChunkFlowers memory = chunk.getExistingDataOrNull(ModAttachments.CHUNK_FLOWERS.get());
        if (memory == null) {
            return;
        }
        for (BlockPos pos : memory.wiltedPositions()) {
            BlockState flower = memory.peekWilted(pos);
            if (flower == null || !blocks.get(pos).isAir() || !blocks.get(pos.below()).is(BlockTags.DIRT)
                    || (isTall(flower) && !blocks.get(pos.above()).isAir())) {
                continue; // something else is there now, or no room yet
            }
            Holder<Biome> biome = biomeAt(chunk, pos);
            boolean comesBack = SnowCover.canBury(flower)
                    ? WorldClimate.dailyMeanAt(level, biome, pos) > SnowAndIce.THAW_TEMPERATURE // grass: once it thaws
                    : bloomsNow(flower, BiomeZones.of(biome.value()).zone(), yearPosition);
            if (comesBack) {
                placeFlower(blocks, pos, flower);
                memory.takeWilted(pos);
            }
        }
    }

    private static void placeFlower(ChunkBlocks blocks, BlockPos pos, BlockState flower) {
        blocks.set(pos, flower);
        if (isTall(flower)) {
            blocks.set(pos.above(), flower.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER));
        }
    }

    // --- Leaf litter and snow on the forest floor -------------------------------------------------------------

    private static void settleForestFloor(ChunkBlocks blocks, BlockPos ground, BlockPos top, Holder<Biome> biome, ClimateZone zone,
                                          double yearPosition, long missed, boolean snowy, RandomSource random) {
        if (!Config.leafLitter() || top.getY() <= ground.getY() + 1 || !blocks.get(ground).is(Blocks.GRASS_BLOCK)
                || !biome.is(ModTags.Biomes.HAS_LEAF_LITTER)) {
            return;
        }
        BlockState treeTop = blocks.get(top);
        if (!treeTop.is(ModTags.Blocks.DROPS_LEAF_LITTER)
                || (treeTop.hasProperty(LeavesBlock.PERSISTENT) && treeTop.getValue(LeavesBlock.PERSISTENT))) {
            return; // not under a natural deciduous tree
        }
        BlockPos floor = ground.above();
        BlockState floorState = blocks.get(floor);
        boolean hasLitter = floorState.is(Blocks.LEAF_LITTER);
        boolean grass = SnowCover.canBury(floorState);
        if ((!hasLitter && !grass && !floorState.isAir()) || random.nextDouble() >= LeafLitterRules.catchUpChance(missed)) {
            return;
        }
        if (snowy) {
            if (grass) {
                buryPlant(blocks, floor, floorState);
            } else {
                placeSnow(blocks, floor); // snow over the fallen leaves
            }
            return;
        }
        if (grass) {
            return; // grass under the trees stays; leaves only gather on bare ground
        }
        double expected = LeafLitterRules.expectedLayers(position -> SeasonalColors.tint(zone, position).leafAutumn(),
                yearPosition, SeasonDate.DEFAULT_YEAR_LENGTH);
        int target = Math.min(LeafLitterRules.MAX_LAYERS, (int) Math.floor(expected + random.nextDouble()));
        int layers = hasLitter ? floorState.getValue(SegmentableBlock.AMOUNT) : 0;
        if (target == layers) {
            return;
        }
        if (target == 0) {
            blocks.set(floor, Blocks.AIR.defaultBlockState());
        } else {
            BlockState litter = hasLitter ? floorState : Blocks.LEAF_LITTER.defaultBlockState()
                    .setValue(LeafLitterBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
            blocks.set(floor, litter.setValue(SegmentableBlock.AMOUNT, target));
        }
    }

    // --- Icicles ----------------------------------------------------------------------------------------------

    /** Under a snowy roof edge: snow on top, an overhang (open air) right under the roof block. */
    private static void hangIcicles(ChunkBlocks blocks, BlockPos top, long missed, RandomSource random) {
        LevelChunk chunk = blocks.chunk();
        BlockPos roof = blocks.get(top).is(Blocks.SNOW) ? top.below() : top;
        BlockState roofState = blocks.get(roof);
        if (!blocks.get(roof.above()).is(Blocks.SNOW) || roofState.is(BlockTags.LEAVES)
                || !roofState.isFaceSturdy(chunk, roof, Direction.DOWN)
                || random.nextDouble() >= IcicleRules.catchUpFormChance(missed)) {
            return;
        }
        BlockPos pos = roof.below();
        int length = IcicleRules.catchUpLength(missed, random.nextDouble());
        for (int i = 0; i < length && blocks.get(pos).isAir() && pos.getY() > chunk.getMinY(); i++) {
            blocks.set(pos, ModBlocks.ICICLE.get().defaultBlockState());
            pos = pos.below();
        }
    }

    // --- Icicles melting and crops growing (anywhere in the chunk, also inside greenhouses) --------------------

    private static void catchUpIciclesAndCrops(ServerLevel level, ChunkBlocks blocks, long missed, RandomSource random) {
        LevelChunk chunk = blocks.chunk();
        LevelChunkSection[] sections = chunk.getSections();
        long today = today(level);
        for (int index = 0; index < sections.length; index++) {
            LevelChunkSection section = sections[index];
            // maybeHas checks the section's block palette: most sections have neither, so they're skipped at once.
            boolean icicles = section.maybeHas(state -> state.is(ModBlocks.ICICLE.get()));
            boolean crops = section.maybeHas(state -> growthOf(state) != null);
            if (!icicles && !crops) {
                continue;
            }
            int baseY = chunk.getSectionYFromSectionIndex(index) * 16;
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        BlockState state = section.getBlockState(x, y, z);
                        BlockPos pos = new BlockPos(chunk.getPos().getBlockX(x), baseY + y, chunk.getPos().getBlockZ(z));
                        if (icicles && state.is(ModBlocks.ICICLE.get())) {
                            meltIcicle(level, blocks, pos, state);
                        } else if (crops && growthOf(state) instanceof Growth growth) {
                            growCrop(blocks, pos, state, growth, missed, today, random);
                        }
                    }
                }
            }
        }
    }

    private static void meltIcicle(ServerLevel level, ChunkBlocks blocks, BlockPos pos, BlockState state) {
        if (state.getValue(IcicleBlock.PERSISTENT)) {
            return; // placed by a player
        }
        double dailyMean = WorldClimate.dailyMeanAt(level, biomeAt(blocks.chunk(), pos), pos);
        if (!Config.icicles() || IcicleRules.meltChance(dailyMean) > 0) {
            blocks.set(pos, Blocks.AIR.defaultBlockState());
        }
    }

    /** How a plant grows: how old it is, the same plant at another age, and the age where it's fully grown. */
    private record Growth(ToIntFunction<BlockState> ageOf, BiFunction<BlockState, Integer, BlockState> withAge, int maxAge) {
        static Growth of(IntegerProperty age, int maxAge) {
            return new Growth(state -> state.getValue(age), (state, value) -> state.setValue(age, value), maxAge);
        }
    }

    /**
     * Crops (wheat, carrots, modded crops…), melon and pumpkin stems, sweet berry bushes and cocoa pods.
     * In vanilla they all grow about as fast (roughly 1 in 5 random ticks on a normal farm), so they share
     * {@link CropSeasonRules#catchUpStages}. Null for anything else.
     */
    private static Growth growthOf(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof CropBlock crop) {
            return new Growth(crop::getAge, (state1, value) -> crop.getStateForAge(value), crop.getMaxAge());
        }
        if (block instanceof StemBlock) {
            return Growth.of(StemBlock.AGE, StemBlock.MAX_AGE);
        }
        if (block instanceof SweetBerryBushBlock) {
            return Growth.of(SweetBerryBushBlock.AGE, SweetBerryBushBlock.MAX_AGE);
        }
        if (block instanceof CocoaBlock) {
            return Growth.of(CocoaBlock.AGE, CocoaBlock.MAX_AGE);
        }
        return null;
    }

    /**
     * A crop grows for every missed day it was in season (out of season only as fast as the config allows).
     * A full-grown melon or pumpkin stem uses the growth that's left to grow its fruit next to it.
     */
    private static void growCrop(ChunkBlocks blocks, BlockPos pos, BlockState state, Growth growth, long missed, long today,
                                 RandomSource random) {
        int age = growth.ageOf().applyAsInt(state);
        if (age >= growth.maxAge() && !(state.getBlock() instanceof StemBlock)) {
            return;
        }
        EnumSet<Season> seasons = CropSeasons.seasonsFor(state);
        ClimateZone zone = BiomeZones.of(biomeAt(blocks.chunk(), pos).value()).zone();
        boolean greenhouse = seasons != null && CropSeasons.isGreenhouse(blocks.chunk(), pos);
        double growingDays = 0;
        for (long day = today - missed; day < today; day++) {
            Season season = SeasonDate.fromDay(day, SeasonDate.DEFAULT_YEAR_LENGTH).season();
            boolean inSeason = seasons == null || CropSeasonRules.isInSeason(seasons, season, zone, () -> greenhouse);
            growingDays += inSeason ? 1.0 : Config.outOfSeasonGrowth();
        }
        int stages = CropSeasonRules.catchUpStages(growingDays, random.nextDouble());
        if (stages <= 0) {
            return;
        }
        if (age < growth.maxAge()) {
            blocks.set(pos, growth.withAge().apply(state, Math.min(growth.maxAge(), age + stages)));
        }
        int leftOver = age + stages - growth.maxAge();
        if (leftOver > 0 && state.getBlock() instanceof StemBlock) {
            growFruit(blocks, pos, state.getBlock(), leftOver, random);
        }
    }

    /**
     * Vanilla: every growth chance a full-grown stem gets, it picks a random side and puts its fruit there if that
     * spot is empty with dirt or farmland under it. Only sides inside this chunk (a loading chunk can't touch its
     * neighbours). Melon and pumpkin only: we know which fruit and attached stem belong to them.
     */
    private static void growFruit(ChunkBlocks blocks, BlockPos stemPos, Block stem, int tries, RandomSource random) {
        Block fruit = stem == Blocks.MELON_STEM ? Blocks.MELON : stem == Blocks.PUMPKIN_STEM ? Blocks.PUMPKIN : null;
        Block attached = stem == Blocks.MELON_STEM ? Blocks.ATTACHED_MELON_STEM : Blocks.ATTACHED_PUMPKIN_STEM;
        if (fruit == null) {
            return;
        }
        ChunkPos chunkPos = blocks.chunk().getPos();
        for (int i = 0; i < Math.min(tries, 4); i++) {
            Direction side = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos fruitPos = stemPos.relative(side);
            if ((fruitPos.getX() >> 4) != chunkPos.x() || (fruitPos.getZ() >> 4) != chunkPos.z()) {
                continue;
            }
            BlockState below = blocks.get(fruitPos.below());
            if (blocks.get(fruitPos).isAir() && (below.is(BlockTags.DIRT) || below.is(Blocks.FARMLAND))) {
                blocks.set(fruitPos, fruit.defaultBlockState());
                blocks.set(stemPos, attached.defaultBlockState().setValue(AttachedStemBlock.FACING, side));
                return;
            }
        }
    }
}
