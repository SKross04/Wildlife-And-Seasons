package net.skross04.wildlifeandseasons.nature;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.skross04.wildlifeandseasons.Config;
import net.skross04.wildlifeandseasons.block.ModBlocks;
import net.skross04.wildlifeandseasons.block.custom.DesertFlowerBlock;
import net.skross04.wildlifeandseasons.climate.BiomeZones;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.season.SeasonCalendar;
import net.skross04.wildlifeandseasons.ecology.season.SeasonDate;
import net.skross04.wildlifeandseasons.ecology.vegetation.DesertBloomRules;
import net.skross04.wildlifeandseasons.season.SeasonSavedData;
import net.skross04.wildlifeandseasons.util.ModTags;

/**
 * The desert bloom in the world. Sand doesn't get random ticks, so every ticking desert chunk looks at a random
 * column now and then (as often as vanilla checks a column for snowfall, faster with a higher randomTickSpeed):
 * it notes desert rain, and while a bloom is on it may grow a wild flower on bare sand there.
 * The flowers themselves die off when it's over ({@link #onWildFlowerRandomTick}).
 */
public final class DesertBloom {
    /** The flowers of the bloom (see DesertBloomRules#kind for how they're mixed). */
    private static final List<Supplier<? extends DesertFlowerBlock>> FLOWERS =
            List.of(ModBlocks.DESERT_SUNFLOWER, ModBlocks.SAND_VERBENA, ModBlocks.DUNE_PRIMROSE);

    private DesertBloom() {
    }

    /** Called for every chunk that gets random ticks, every tick. */
    public static void onChunkTick(ServerLevel level, LevelChunk chunk, int randomTickSpeed) {
        // On average one column per chunk every 16 ticks at the default randomTickSpeed of 3.
        if (!Config.desertBloom() || level.getRandom().nextInt(48) >= randomTickSpeed) {
            return;
        }
        RandomSource random = level.getRandom();
        int x = chunk.getPos().getBlockX(random.nextInt(16));
        int z = chunk.getPos().getBlockZ(random.nextInt(16));
        BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(x, 0, z));
        Holder<Biome> biome = level.getBiome(top);
        if (!biome.is(ModTags.Biomes.HAS_DESERT_BLOOM) || BiomeZones.of(biome.value()).zone() != ClimateZone.ARID) {
            return;
        }
        SeasonCalendar calendar = SeasonSavedData.get(level.getServer()).calendar();
        long today = calendar.dayIndex();
        DesertBloomSavedData bloom = DesertBloomSavedData.get(level.getServer());
        if (level.isRainingAt(top)) {
            bloom.rainedOn(today);
            return;
        }
        double yearPosition = calendar.yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH);
        if (!DesertBloomRules.sprouting(bloom.daysSinceRain(today), yearPosition)
                || !level.getBlockState(top).isAir() || !level.getBlockState(top.below()).is(BlockTags.SAND)
                || random.nextDouble() >= DesertBloomRules.sproutChance(bloom.rainyDays(), x, z)) {
            return;
        }
        int kind = DesertBloomRules.kind(FLOWERS.size(), x, z, random.nextDouble());
        BlockState flower = FLOWERS.get(kind).get().defaultBlockState();
        if (flower.canSurvive(level, top)) {
            level.setBlock(top, flower, Block.UPDATE_ALL);
        }
    }

    /** A wild desert flower: when the bloom is over (or switched off) it dries up and is gone within about a day. */
    public static void onWildFlowerRandomTick(ServerLevel level, BlockPos pos, RandomSource random) {
        SeasonCalendar calendar = SeasonSavedData.get(level.getServer()).calendar();
        long daysSinceRain = DesertBloomSavedData.get(level.getServer()).daysSinceRain(calendar.dayIndex());
        boolean over = !Config.desertBloom()
                || DesertBloomRules.wilting(daysSinceRain, calendar.yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH));
        if (over && random.nextDouble() < DesertBloomRules.WILT_CHANCE) {
            level.removeBlock(pos, false);
        }
    }
}
