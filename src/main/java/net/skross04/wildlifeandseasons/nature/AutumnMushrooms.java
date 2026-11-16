package net.skross04.wildlifeandseasons.nature;

import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.ShelfMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.skross04.wildlifeandseasons.Config;
import net.skross04.wildlifeandseasons.attachment.ModAttachments;
import net.skross04.wildlifeandseasons.climate.WorldClimate;
import net.skross04.wildlifeandseasons.ecology.season.SeasonCalendar;
import net.skross04.wildlifeandseasons.ecology.season.SeasonDate;
import net.skross04.wildlifeandseasons.ecology.vegetation.MushroomRules;
import net.skross04.wildlifeandseasons.season.SeasonSavedData;
import net.skross04.wildlifeandseasons.util.ModTags;

/**
 * Autumn mushrooms: brown and red mushrooms come up on shady forest floors in autumn, mostly after rain, and shelf
 * mushrooms grow on tree trunks. The first frosts make them disappear again. Uses the grass blocks' random ticks.
 * <p>
 * Only mushrooms grown here are remembered (in the chunk's {@link ChunkFlowers}) and removed again; mushrooms from
 * world generation or placed by players stay.
 */
public final class AutumnMushrooms {
    /** Shelf mushrooms grow this many blocks up a trunk at most (counted from the ground next to it). */
    private static final int SHELF_MAX_HEIGHT = 3;

    /** The calendar day it last rained, per dimension (only needs to be roughly right). */
    private static final Map<ResourceKey<Level>, Long> LAST_RAIN_DAY = new HashMap<>();

    private AutumnMushrooms() {
    }

    public static void onGrassRandomTick(BlockState state, ServerLevel level, BlockPos ground, RandomSource random) {
        if (!Config.autumnMushrooms() || !state.is(Blocks.GRASS_BLOCK)) {
            return;
        }
        SeasonCalendar calendar = SeasonSavedData.get(level.getServer()).calendar();
        long today = calendar.dayIndex();
        Holder<Biome> biome = level.getBiome(ground);
        boolean raining = level.isRaining()
                && biome.value().getPrecipitationAt(ground.above(), level.getSeaLevel()) == Biome.Precipitation.RAIN;
        if (raining) {
            LAST_RAIN_DAY.put(level.dimension(), today);
        }

        double dailyMean = WorldClimate.dailyMeanAt(level, ground);
        LevelChunk chunk = level.getChunkAt(ground);
        ChunkFlowers memory = chunk.getExistingDataOrNull(ModAttachments.CHUNK_FLOWERS.get());
        if (memory != null && memory.hasMushrooms() && ageRememberedMushrooms(level, ground, memory, dailyMean, random)) {
            chunk.markUnsaved();
            return;
        }

        if (!biome.is(ModTags.Biomes.HAS_AUTUMN_MUSHROOMS)) {
            return;
        }
        double fruiting = MushroomRules.fruiting(dailyMean, calendar.yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH));
        if (fruiting <= 0) {
            return;
        }
        Long lastRain = LAST_RAIN_DAY.get(level.dimension());
        double daysSinceRain = lastRain == null ? -1 : today - lastRain;
        double chance = fruiting * MushroomRules.wetness(raining, daysSinceRain);
        if (random.nextDouble() < MushroomRules.FRUIT_CHANCE * chance) {
            growGroundMushroom(level, ground, random);
        } else if (random.nextDouble() < MushroomRules.SHELF_CHANCE * chance) {
            growShelfMushroom(level, ground, random);
        }
    }

    /**
     * Mushrooms grown earlier above this grass block (on the ground, or as shelf mushrooms on a trunk beside it)
     * rot away after about a week and collapse in frost; small shelf mushrooms grow bigger. True if anything changed.
     */
    private static boolean ageRememberedMushrooms(ServerLevel level, BlockPos ground, ChunkFlowers memory, double dailyMean,
                                                  RandomSource random) {
        boolean frost = MushroomRules.frost(dailyMean);
        boolean changed = false;
        for (int height = 1; height <= SHELF_MAX_HEIGHT; height++) {
            BlockPos pos = ground.above(height);
            if (!memory.isSeasonalMushroom(pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (isGroundMushroom(state)) {
                if (random.nextDouble() < (frost ? MushroomRules.FROST_CHANCE : MushroomRules.ROT_CHANCE)) {
                    level.removeBlock(pos, false);
                    memory.removeMushroom(pos);
                }
            } else if (state.is(Blocks.SHELF_MUSHROOM)) {
                if (frost && random.nextDouble() < MushroomRules.SHELF_FROST_CHANCE) {
                    level.removeBlock(pos, false);
                    memory.removeMushroom(pos);
                } else if (!frost && state.getValue(ShelfMushroomBlock.AGE) < ShelfMushroomBlock.MAX_AGE
                        && random.nextDouble() < MushroomRules.SHELF_GROW_CHANCE) {
                    level.setBlockAndUpdate(pos, state.setValue(ShelfMushroomBlock.AGE, state.getValue(ShelfMushroomBlock.AGE) + 1));
                }
            } else {
                memory.removeMushroom(pos); // picked or replaced: forget it
            }
            changed = true;
        }
        return changed;
    }

    /** A brown or red mushroom on the grass, in the shade of natural tree leaves. */
    private static void growGroundMushroom(ServerLevel level, BlockPos ground, RandomSource random) {
        BlockPos pos = ground.above();
        if (!level.getBlockState(pos).isAir()) {
            return;
        }
        BlockState treeTop = naturalLeavesAbove(level, ground);
        if (treeTop == null) {
            return; // only in the forest's shade
        }
        boolean flyAgaricTree = treeTop.is(Blocks.BIRCH_LEAVES) || treeTop.is(Blocks.SPRUCE_LEAVES);
        BlockState mushroom = (random.nextDouble() < MushroomRules.redChance(flyAgaricTree)
                ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM).defaultBlockState();
        if (!mushroom.canSurvive(level, pos)) {
            return; // too bright here (mushrooms need light below 13)
        }
        level.setBlockAndUpdate(pos, mushroom);
        level.getChunkAt(pos).getData(ModAttachments.CHUNK_FLOWERS.get()).addMushroom(pos);
    }

    /** A small shelf mushroom on the trunk of a natural tree standing next to this grass block. */
    private static void growShelfMushroom(ServerLevel level, BlockPos ground, RandomSource random) {
        Direction side = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        // A natural tree: a log standing on the ground right beside us, with leaves at its top.
        if (!level.getBlockState(ground.above().relative(side)).is(BlockTags.LOGS)
                || !level.getBlockState(ground.relative(side)).is(BlockTags.DIRT)
                || naturalLeavesAbove(level, ground.relative(side)) == null) {
            return;
        }
        BlockPos pos = ground.above(1 + random.nextInt(SHELF_MAX_HEIGHT));
        if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.relative(side)).is(BlockTags.LOGS)) {
            return;
        }
        BlockState shelf = Blocks.SHELF_MUSHROOM.defaultBlockState().setValue(ShelfMushroomBlock.FACING, side.getOpposite());
        if (!shelf.canSurvive(level, pos)) {
            return;
        }
        level.setBlockAndUpdate(pos, shelf);
        level.getChunkAt(pos).getData(ModAttachments.CHUNK_FLOWERS.get()).addMushroom(pos);
    }

    /** The natural leaves on top of the column above, or null if it's open sky or not a tree. */
    private static @Nullable BlockState naturalLeavesAbove(ServerLevel level, BlockPos ground) {
        BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ground).below();
        if (top.getY() <= ground.getY() + 1) {
            return null;
        }
        BlockState state = level.getBlockState(top);
        if (!state.is(BlockTags.LEAVES) || state.hasProperty(LeavesBlock.PERSISTENT) && state.getValue(LeavesBlock.PERSISTENT)) {
            return null;
        }
        return state;
    }

    static boolean isGroundMushroom(BlockState state) {
        return state.is(Blocks.BROWN_MUSHROOM) || state.is(Blocks.RED_MUSHROOM);
    }
}
