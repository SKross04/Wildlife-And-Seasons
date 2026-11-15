package net.skross04.wildalmanac.nature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeafLitterBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SegmentableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.skross04.wildalmanac.Config;
import net.skross04.wildalmanac.climate.BiomeZones;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;
import net.skross04.wildalmanac.ecology.climate.SeasonalColors;
import net.skross04.wildalmanac.ecology.season.SeasonDate;
import net.skross04.wildalmanac.ecology.vegetation.LeafLitterRules;
import net.skross04.wildalmanac.season.SeasonSavedData;
import net.skross04.wildalmanac.util.ModTags;

/**
 * Fallen leaves on the forest floor, using vanilla's leaf litter block (1–4 layers, crunches when you walk on it).
 * <p>
 * Runs when Minecraft randomly ticks a grass block (see {@code SpreadingSnowyBlockMixin}).
 * Only on grass in forest biomes ({@code wildalmanac:has_leaf_litter}) that has natural leaves of a
 * deciduous tree above it ({@code wildalmanac:drops_leaf_litter}, not placed by a player),
 * so lawns, paths, farmland and builds stay clean. Cold-zone forests (taigas) don't drop leaves.
 * When it snows, snow also settles there and covers the litter (vanilla snow can't get through leaves).
 */
public final class LeafLitter {
    /** Chance per grass tick, while it snows, that snow settles on the forest floor under the trees. */
    private static final double SNOW_COVER_CHANCE = 0.3;

    private LeafLitter() {
    }

    public static void onGrassRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!Config.leafLitter() || !state.is(Blocks.GRASS_BLOCK)) {
            return;
        }
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);
        boolean hasLitter = aboveState.is(Blocks.LEAF_LITTER);
        if (!hasLitter && !aboveState.isAir()) {
            return; // flowers, tall grass, snow… stay as they are
        }
        Holder<Biome> biome = level.getBiome(pos);
        if (!biome.is(ModTags.Biomes.HAS_LEAF_LITTER) || !underDeciduousTree(level, pos)) {
            return;
        }

        // Vanilla snow can't get through the leaves, so a forest floor would stay brown while everything around it
        // is white. When it snows, let snow settle under deciduous trees too (it covers the leaf litter).
        if (level.isRaining() && biome.value().getPrecipitationAt(above, level.getSeaLevel()) == Biome.Precipitation.SNOW) {
            if (random.nextDouble() < SNOW_COVER_CHANCE) {
                level.setBlockAndUpdate(above, Blocks.SNOW.defaultBlockState());
            }
            return;
        }

        double yearPosition = SeasonSavedData.get(level.getServer()).calendar().yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH);
        double autumn = SeasonalColors.tint(BiomeZones.of(biome.value()).zone(), yearPosition).leafAutumn();

        if (hasLitter) {
            int layers = aboveState.getValue(SegmentableBlock.AMOUNT);
            if (random.nextDouble() < LeafLitterRules.rotChance(yearPosition, autumn)) {
                if (layers > 1) {
                    level.setBlockAndUpdate(above, aboveState.setValue(SegmentableBlock.AMOUNT, layers - 1));
                } else {
                    level.removeBlock(above, false);
                }
            } else if (layers < LeafLitterRules.MAX_LAYERS && random.nextDouble() < LeafLitterRules.dropChance(autumn)) {
                level.setBlockAndUpdate(above, aboveState.setValue(SegmentableBlock.AMOUNT, layers + 1));
            }
        } else if (random.nextDouble() < LeafLitterRules.dropChance(autumn)) {
            BlockState litter = Blocks.LEAF_LITTER.defaultBlockState()
                    .setValue(LeafLitterBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random))
                    .setValue(SegmentableBlock.AMOUNT, 1);
            level.setBlockAndUpdate(above, litter);
        }
    }

    /**
     * A chunk nobody was near for {@code missedDays}: bring the litter on this forest floor spot to what it would
     * be by now (more layers after an autumn, none after a spring), and snow over it if it's a snowy winter day.
     */
    public static void catchUp(ServerLevel level, BlockPos ground, RandomSource random, long missedDays, boolean snowy) {
        if (!Config.leafLitter() || !level.getBlockState(ground).is(Blocks.GRASS_BLOCK)) {
            return;
        }
        BlockPos above = ground.above();
        BlockState aboveState = level.getBlockState(above);
        boolean hasLitter = aboveState.is(Blocks.LEAF_LITTER);
        if (!hasLitter && !aboveState.isAir()) {
            return;
        }
        Holder<Biome> biome = level.getBiome(ground);
        if (!biome.is(ModTags.Biomes.HAS_LEAF_LITTER) || !underDeciduousTree(level, ground)
                || random.nextDouble() >= LeafLitterRules.catchUpChance(missedDays)) {
            return;
        }
        if (snowy) {
            level.setBlockAndUpdate(above, Blocks.SNOW.defaultBlockState());
            return;
        }
        ClimateZone zone = BiomeZones.of(biome.value()).zone();
        double yearPosition = SeasonSavedData.get(level.getServer()).calendar().yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH);
        double expected = LeafLitterRules.expectedLayers(position -> SeasonalColors.tint(zone, position).leafAutumn(),
                yearPosition, SeasonDate.DEFAULT_YEAR_LENGTH);
        int target = (int) Math.floor(expected + random.nextDouble()); // 1.4 → 1 or 2 layers, 2 in 40 % of the spots
        int layers = hasLitter ? aboveState.getValue(SegmentableBlock.AMOUNT) : 0;
        if (target == layers) {
            return;
        }
        if (target == 0) {
            level.removeBlock(above, false);
        } else {
            BlockState litter = hasLitter ? aboveState : Blocks.LEAF_LITTER.defaultBlockState()
                    .setValue(LeafLitterBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
            level.setBlockAndUpdate(above, litter.setValue(SegmentableBlock.AMOUNT, Math.min(target, LeafLitterRules.MAX_LAYERS)));
        }
    }

    /** True if the highest solid block above is natural (not player-placed) leaves of a tree that drops its leaves. */
    private static boolean underDeciduousTree(ServerLevel level, BlockPos ground) {
        BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ground).below();
        if (top.getY() <= ground.getY() + 1) {
            return false; // nothing above: open sky
        }
        BlockState treeTop = level.getBlockState(top);
        return treeTop.is(ModTags.Blocks.DROPS_LEAF_LITTER)
                && !(treeTop.hasProperty(LeavesBlock.PERSISTENT) && treeTop.getValue(LeavesBlock.PERSISTENT));
    }
}
