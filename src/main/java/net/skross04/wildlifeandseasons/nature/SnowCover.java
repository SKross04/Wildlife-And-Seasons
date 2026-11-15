package net.skross04.wildlifeandseasons.nature;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.skross04.wildlifeandseasons.attachment.ModAttachments;
import net.skross04.wildlifeandseasons.util.ModTags;

/**
 * Snow covers grass. Vanilla snow only falls on empty spots, so every tuft of grass or fern leaves a hole in the
 * snow all winter. Here, falling snow buries those plants ({@code wildlifeandseasons:buried_by_snow}); the chunk
 * remembers what grew where (with the wilted flowers in {@link ChunkFlowers}) and the plant grows back in the same
 * spot when the snow melts in spring.
 */
public final class SnowCover {
    private SnowCover() {
    }

    public static boolean canBury(BlockState state) {
        return state.is(ModTags.Blocks.BURIED_BY_SNOW);
    }

    /**
     * Called for each snowfall attempt (see {@code ServerLevelMixin}, before vanilla's own snowfall): if it snows on
     * a column whose top is a buried-by-snow plant, the plant is covered by a snow layer.
     */
    public static void onSnowfall(ServerLevel level, BlockPos column) {
        if (!level.isRaining()) {
            return;
        }
        BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, column);
        BlockState plant = level.getBlockState(pos);
        if (!canBury(plant)) {
            return;
        }
        Biome biome = level.getBiome(pos).value();
        if (biome.getPrecipitationAt(pos, level.getSeaLevel()) == Biome.Precipitation.SNOW
                && level.getBrightness(LightLayer.BLOCK, pos) < 10) {
            bury(level, pos, plant);
        }
    }

    /** Covers a plant with a snow layer and remembers it. The top half of tall grass goes too (vanilla does that). */
    public static void bury(ServerLevel level, BlockPos pos, BlockState plant) {
        LevelChunk chunk = level.getChunkAt(pos);
        chunk.getData(ModAttachments.CHUNK_FLOWERS.get()).addWilted(pos, plant);
        chunk.markUnsaved();
        level.setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());
    }

    /**
     * The snow here just melted away (see {@code SnowLayerBlockMixin}): the grass that was under it comes back
     * straight away, so spring meadows don't look bare.
     */
    public static void onSnowMelted(ServerLevel level, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        ChunkFlowers memory = chunk.getExistingDataOrNull(ModAttachments.CHUNK_FLOWERS.get());
        if (memory == null) {
            return;
        }
        BlockState plant = memory.peekWilted(pos);
        if (plant == null || !canBury(plant) || !level.getBlockState(pos).isAir() || !plant.canSurvive(level, pos)) {
            return;
        }
        if (plant.getBlock() instanceof DoublePlantBlock) {
            if (!level.getBlockState(pos.above()).isAir()) {
                return; // no room for the top half: it comes back later with the ground's random ticks
            }
            DoublePlantBlock.placeAt(level, plant, pos, Block.UPDATE_ALL);
        } else {
            level.setBlockAndUpdate(pos, plant);
        }
        memory.takeWilted(pos);
        chunk.markUnsaved();
    }
}
