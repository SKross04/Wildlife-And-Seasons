package net.skross04.wildlifeandseasons.nature;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.attachment.ModAttachments;
import net.skross04.wildlifeandseasons.block.ModBlocks;
import net.skross04.wildlifeandseasons.block.entity.DormantFlowerBlockEntity;
import net.skross04.wildlifeandseasons.climate.BiomeZones;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.season.SeasonDate;
import net.skross04.wildlifeandseasons.ecology.vegetation.FlowerRules;
import net.skross04.wildlifeandseasons.season.SeasonSavedData;
import net.skross04.wildlifeandseasons.util.ModTags;

/**
 * Flowers bloom in spring, tall flowers in spring and summer (see {@link FlowerRules}).
 * <ul>
 *     <li>Wild flowers wilt after their bloom time and disappear; the chunk remembers them and they regrow in spring.</li>
 *     <li>Flowers a player planted become a dormant flower (closed bud) and open again in spring.</li>
 *     <li>Flower pots are other blocks, so potted flowers never change.</li>
 * </ul>
 * Runs on the random ticks of the grass the flower stands on, so it happens gradually.
 */
@EventBusSubscriber(modid = WildlifeAndSeasons.MODID)
public final class SeasonalFlowers {
    private SeasonalFlowers() {
    }

    /** Seasonal = in {@code wildlifeandseasons:seasonal_flowers} and not in {@code wildlifeandseasons:not_seasonal_flowers}. */
    public static boolean isSeasonalFlower(BlockState state) {
        return state.is(ModTags.Blocks.SEASONAL_FLOWERS) && !state.is(ModTags.Blocks.NOT_SEASONAL_FLOWERS);
    }

    /** The bottom half of a seasonal tall flower ({@code wildlifeandseasons:seasonal_tall_flowers}), e.g. a lilac. */
    public static boolean isSeasonalTallFlower(BlockState state) {
        return state.is(ModTags.Blocks.SEASONAL_TALL_FLOWERS) && !state.is(ModTags.Blocks.NOT_SEASONAL_FLOWERS)
                && state.hasProperty(DoublePlantBlock.HALF) && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER;
    }

    private static boolean isTall(BlockState flower) {
        return flower.getBlock() instanceof DoublePlantBlock;
    }

    /** Called when the grass under {@code ground} gets a random tick. */
    public static void onGroundRandomTick(ServerLevel level, BlockPos ground, RandomSource random) {
        update(level, ground, random, false);
    }

    /** A chunk nobody was near for a while: settle the flower here straight away (see {@link ChunkCatchUp}). */
    public static void catchUp(ServerLevel level, BlockPos ground, RandomSource random) {
        update(level, ground, random, true);
    }

    private static void update(ServerLevel level, BlockPos ground, RandomSource random, boolean catchUp) {
        double wiltChance = catchUp ? 1.0 : FlowerRules.WILT_CHANCE;
        double bloomChance = catchUp ? 1.0 : FlowerRules.BLOOM_CHANCE;
        BlockPos pos = ground.above();
        BlockState state = level.getBlockState(pos);
        boolean flower = isSeasonalFlower(state) || isSeasonalTallFlower(state);
        boolean dormant = state.is(ModBlocks.DORMANT_FLOWER.get());
        LevelChunk chunk = level.getChunkAt(pos);
        ChunkFlowers memory = chunk.getExistingDataOrNull(ModAttachments.CHUNK_FLOWERS.get());
        boolean wiltedHere = state.isAir() && memory != null && memory.hasWilted(pos);
        if (!flower && !dormant && !wiltedHere) {
            return; // nothing flowery here: the usual case, so stop as early as possible
        }

        double yearPosition = SeasonSavedData.get(level.getServer()).calendar().yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH);
        ClimateZone zone = BiomeZones.of(level.getBiome(pos).value()).zone();
        boolean bloomTime = FlowerRules.isBloomTime(zone, yearPosition);
        boolean tallBloomTime = FlowerRules.isTallBloomTime(zone, yearPosition);

        if (flower) {
            if ((isTall(state) ? tallBloomTime : bloomTime) || random.nextDouble() >= wiltChance) {
                return;
            }
            ChunkFlowers data = chunk.getData(ModAttachments.CHUNK_FLOWERS.get());
            // Replacing the bottom half also removes the top half of a tall flower (vanilla does that).
            if (data.isPlanted(pos)) {
                level.setBlockAndUpdate(pos, ModBlocks.DORMANT_FLOWER.get().defaultBlockState());
                if (level.getBlockEntity(pos) instanceof DormantFlowerBlockEntity bud) {
                    bud.setFlower(state);
                }
            } else {
                data.addWilted(pos, state);
                level.removeBlock(pos, false);
            }
            chunk.markUnsaved();
        } else if (dormant) {
            if (level.getBlockEntity(pos) instanceof DormantFlowerBlockEntity bud
                    && (isTall(bud.getFlower()) ? tallBloomTime : bloomTime)
                    && random.nextDouble() < bloomChance) {
                regrow(level, pos, bud.getFlower());
            }
        } else if (random.nextDouble() < bloomChance) {
            BlockState wilted = memory.peekWilted(pos);
            if (wilted != null && (isTall(wilted) ? tallBloomTime : bloomTime) && regrow(level, pos, wilted)) {
                memory.takeWilted(pos);
                chunk.markUnsaved();
            }
        }
    }

    /** Puts a flower back (both halves for a tall one). False when it doesn't fit or can't stand here (yet). */
    private static boolean regrow(ServerLevel level, BlockPos pos, BlockState flower) {
        if (!flower.canSurvive(level, pos)) {
            return false;
        }
        if (isTall(flower)) {
            if (!level.getBlockState(pos.above()).isAir()) {
                return false; // no room for the top half: try again later
            }
            DoublePlantBlock.placeAt(level, flower, pos, Block.UPDATE_ALL);
        } else {
            level.setBlockAndUpdate(pos, flower);
        }
        return true;
    }

    /** Remember flowers that players plant (also a wild flower that was picked and replanted). */
    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockPos pos = event.getPos();
        LevelChunk chunk = level.getChunkAt(pos);
        BlockState placed = event.getPlacedBlock();
        boolean planting = event.getEntity() instanceof Player && (isSeasonalFlower(placed) || isSeasonalTallFlower(placed));
        ChunkFlowers memory = planting
                ? chunk.getData(ModAttachments.CHUNK_FLOWERS.get())
                : chunk.getExistingDataOrNull(ModAttachments.CHUNK_FLOWERS.get());
        if (memory == null) {
            return;
        }
        memory.takeWilted(pos); // something new is here now: the wild flower won't come back
        if (planting) {
            memory.plant(pos);
        }
        chunk.markUnsaved();
    }

    @SubscribeEvent
    public static void onBlockBroken(BreakBlockEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            LevelChunk chunk = level.getChunkAt(event.getPos());
            ChunkFlowers memory = chunk.getExistingDataOrNull(ModAttachments.CHUNK_FLOWERS.get());
            if (memory == null) {
                return;
            }
            // Breaking the top half of a planted tall flower: the flower was remembered at its bottom half.
            BlockState broken = level.getBlockState(event.getPos());
            BlockPos flowerPos = broken.hasProperty(DoublePlantBlock.HALF)
                    && broken.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? event.getPos().below() : event.getPos();
            if (memory.isPlanted(flowerPos)) {
                memory.unplant(flowerPos);
                chunk.markUnsaved();
            }
        }
    }
}
