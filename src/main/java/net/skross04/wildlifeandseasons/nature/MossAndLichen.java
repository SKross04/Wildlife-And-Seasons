package net.skross04.wildlifeandseasons.nature;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.skross04.wildlifeandseasons.Config;
import net.skross04.wildlifeandseasons.block.ModBlocks;
import net.skross04.wildlifeandseasons.climate.BiomeZones;
import net.skross04.wildlifeandseasons.climate.WorldClimate;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.vegetation.MossRules;
import net.skross04.wildlifeandseasons.ecology.vegetation.MossRules.Face;
import net.skross04.wildlifeandseasons.util.ModTags;

/**
 * Moss and lichen slowly take over stone, over the in-game years:
 * <ul>
 *   <li>old cobblestone and stone bricks in generated structures (villages, ruins, strongholds…) turn mossy, mostly
 *       on the shady, damp sides (player builds only if {@code mossOnPlayerBuilds} is on);</li>
 *   <li>lichen grows on open natural rock (mountains, cliffs, boulders), as our own lichen block on the rock face;</li>
 *   <li>moss carpets grow on top of rocks in the shade of trees.</li>
 * </ul>
 * Stone doesn't get random ticks, so every ticking chunk looks at a few random blocks per tick (randomTickSpeed / 3,
 * so about one at the default and faster with a higher randomTickSpeed). Nothing is ever destroyed: an axe scrapes
 * mossy blocks clean again (NeoForge transformables data map), lichen and carpets break like vanilla ones.
 */
public final class MossAndLichen {
    /** Clean block → its mossy version (vanilla's mossy cobblestone and stone brick families). */
    public static final Map<Block, Block> MOSSY = Map.ofEntries(
            Map.entry(Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE),
            Map.entry(Blocks.COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE_STAIRS),
            Map.entry(Blocks.COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB),
            Map.entry(Blocks.COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE_WALL),
            Map.entry(Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS),
            Map.entry(Blocks.STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS),
            Map.entry(Blocks.STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB),
            Map.entry(Blocks.STONE_BRICK_WALL, Blocks.MOSSY_STONE_BRICK_WALL));

    private static final Direction[] DIRECTIONS = Direction.values();

    private MossAndLichen() {
    }

    /** Called for every chunk that gets random ticks, every tick. */
    public static void onChunkTick(ServerLevel level, LevelChunk chunk, int randomTickSpeed) {
        if (!Config.mossAndLichen() || randomTickSpeed <= 0) {
            return;
        }
        RandomSource random = level.getRandom();
        double samples = randomTickSpeed / 3.0;
        int count = (int) samples + (random.nextDouble() < samples - (int) samples ? 1 : 0);
        LevelChunkSection[] sections = chunk.getSections();
        for (int i = 0; i < count; i++) {
            int index = random.nextInt(sections.length);
            if (sections[index].hasOnlyAir()) {
                continue;
            }
            int x = random.nextInt(16), y = random.nextInt(16), z = random.nextInt(16);
            BlockState state = sections[index].getBlockState(x, y, z);
            boolean mossable = MOSSY.containsKey(state.getBlock());
            if (!mossable && !state.is(ModTags.Blocks.NATURAL_ROCK)) {
                continue; // most samples: dirt, buried stone, air… nothing to do
            }
            BlockPos pos = new BlockPos(chunk.getPos().getBlockX(x), chunk.getSectionYFromSectionIndex(index) * 16 + y,
                    chunk.getPos().getBlockZ(z));
            Direction side = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos air = pos.relative(side);
            BlockState outside = level.getBlockState(air);
            if (mossable) {
                if (outside.isAir() || outside.is(ModBlocks.LICHEN.get())) {
                    growMoss(level, pos, state, side, air, random);
                }
            } else if (side == Direction.UP && outside.isAir() && underTrees(level, air)) {
                growMossCarpet(level, pos, air, random);
            } else if (outside.isAir() || outside.is(ModBlocks.LICHEN.get())) {
                growLichen(level, pos, side, air, outside, random);
            }
        }
    }

    private static void growMoss(ServerLevel level, BlockPos pos, BlockState state, Direction side, BlockPos air,
                                 RandomSource random) {
        double chance = MossRules.mossChance(zone(level, pos), WorldClimate.dailyMeanAt(level, pos),
                level.isRainingAt(air.above()) || level.isRainingAt(air), face(side),
                level.getBrightness(LightLayer.SKY, air), neighbours(level, pos, MossAndLichen::isMossy));
        // Structure check last: it's the slowest, and most samples fail the chance anyway.
        if (random.nextDouble() >= chance || !Config.mossOnPlayerBuilds() && !inStructure(level, pos)) {
            return;
        }
        level.setBlock(pos, MOSSY.get(state.getBlock()).withPropertiesOf(state), Block.UPDATE_ALL);
    }

    private static void growLichen(ServerLevel level, BlockPos rock, Direction side, BlockPos air, BlockState outside,
                                   RandomSource random) {
        Direction towardsRock = side.getOpposite();
        if (outside.is(ModBlocks.LICHEN.get()) && MultifaceBlock.hasFace(outside, towardsRock)) {
            return;
        }
        double chance = MossRules.lichenChance(zone(level, rock), WorldClimate.dailyMeanAt(level, rock),
                level.isRainingAt(air), face(side), level.getBrightness(LightLayer.SKY, air),
                neighbours(level, air, s -> s.is(ModBlocks.LICHEN.get())), rock.getX(), rock.getY(), rock.getZ());
        if (random.nextDouble() >= chance) {
            return;
        }
        BlockState lichen = outside.is(ModBlocks.LICHEN.get()) ? outside : ModBlocks.LICHEN.get().defaultBlockState();
        lichen = lichen.setValue(MultifaceBlock.getFaceProperty(towardsRock), true);
        if (lichen.canSurvive(level, air)) {
            level.setBlock(air, lichen, Block.UPDATE_ALL);
        }
    }

    private static void growMossCarpet(ServerLevel level, BlockPos rock, BlockPos air, RandomSource random) {
        double chance = MossRules.mossCarpetChance(zone(level, rock), WorldClimate.dailyMeanAt(level, rock),
                level.isRaining(), neighbours(level, air, s -> s.is(Blocks.MOSS_CARPET)));
        if (random.nextDouble() < chance && Blocks.MOSS_CARPET.defaultBlockState().canSurvive(level, air)) {
            level.setBlock(air, Blocks.MOSS_CARPET.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    /** Shade of trees: the highest block above this spot is natural leaves (so not under a player's roof). */
    private static boolean underTrees(ServerLevel level, BlockPos air) {
        BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, air).below();
        if (top.getY() <= air.getY()) {
            return false;
        }
        BlockState canopy = level.getBlockState(top);
        return canopy.is(BlockTags.LEAVES) && !canopy.getOptionalValue(
                net.minecraft.world.level.block.LeavesBlock.PERSISTENT).orElse(true);
    }

    private static boolean isMossy(BlockState state) {
        return MOSSY.containsValue(state.getBlock()) || state.is(Blocks.MOSS_BLOCK);
    }

    /** Inside a piece of a generated structure (village, ruin, stronghold, trail ruins…). */
    private static boolean inStructure(ServerLevel level, BlockPos pos) {
        return level.structureManager().getStructureWithPieceAt(pos, structure -> true).isValid();
    }

    private static int neighbours(ServerLevel level, BlockPos pos, java.util.function.Predicate<BlockState> test) {
        int count = 0;
        for (Direction direction : DIRECTIONS) {
            if (test.test(level.getBlockState(pos.relative(direction)))) {
                count++;
            }
        }
        return count;
    }

    private static ClimateZone zone(ServerLevel level, BlockPos pos) {
        return BiomeZones.of(level.getBiome(pos).value()).zone();
    }

    private static Face face(Direction side) {
        return switch (side) {
            case UP -> Face.UP;
            case DOWN -> Face.DOWN;
            case NORTH -> Face.NORTH;
            case SOUTH -> Face.SOUTH;
            case EAST -> Face.EAST;
            case WEST -> Face.WEST;
        };
    }
}
