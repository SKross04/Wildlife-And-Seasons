package net.skross04.wildalmanac.nature;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.attachment.ModAttachments;
import net.skross04.wildalmanac.block.ModBlocks;
import net.skross04.wildalmanac.block.entity.DormantFlowerBlockEntity;
import net.skross04.wildalmanac.climate.BiomeZones;
import net.skross04.wildalmanac.ecology.season.SeasonDate;
import net.skross04.wildalmanac.ecology.vegetation.FlowerRules;
import net.skross04.wildalmanac.season.SeasonSavedData;
import net.skross04.wildalmanac.util.ModTags;

/**
 * Flowers bloom in spring (see {@link FlowerRules}).
 * <ul>
 *     <li>Wild flowers wilt after their bloom time and disappear; the chunk remembers them and they regrow in spring.</li>
 *     <li>Flowers a player planted become a dormant flower (closed bud) and open again in spring.</li>
 *     <li>Flower pots are other blocks, so potted flowers never change.</li>
 * </ul>
 * Runs on the random ticks of the grass the flower stands on, so it happens gradually.
 */
@EventBusSubscriber(modid = WildAlmanac.MODID)
public final class SeasonalFlowers {
    private SeasonalFlowers() {
    }

    /** Seasonal = in {@code wildalmanac:seasonal_flowers} and not in {@code wildalmanac:not_seasonal_flowers}. */
    public static boolean isSeasonalFlower(BlockState state) {
        return state.is(ModTags.Blocks.SEASONAL_FLOWERS) && !state.is(ModTags.Blocks.NOT_SEASONAL_FLOWERS);
    }

    /** Called when the grass under {@code ground} gets a random tick. */
    public static void onGroundRandomTick(ServerLevel level, BlockPos ground, RandomSource random) {
        BlockPos pos = ground.above();
        BlockState state = level.getBlockState(pos);
        boolean flower = isSeasonalFlower(state);
        boolean dormant = state.is(ModBlocks.DORMANT_FLOWER.get());
        LevelChunk chunk = level.getChunkAt(pos);
        ChunkFlowers memory = chunk.getExistingDataOrNull(ModAttachments.CHUNK_FLOWERS.get());
        boolean wiltedHere = state.isAir() && memory != null && memory.hasWilted(pos);
        if (!flower && !dormant && !wiltedHere) {
            return; // nothing flowery here: the usual case, so stop as early as possible
        }

        double yearPosition = SeasonSavedData.get(level.getServer()).calendar().yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH);
        boolean bloomTime = FlowerRules.isBloomTime(BiomeZones.of(level.getBiome(pos).value()).zone(), yearPosition);

        if (flower && !bloomTime && random.nextDouble() < FlowerRules.WILT_CHANCE) {
            ChunkFlowers data = chunk.getData(ModAttachments.CHUNK_FLOWERS.get());
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
        } else if (dormant && bloomTime && random.nextDouble() < FlowerRules.BLOOM_CHANCE) {
            if (level.getBlockEntity(pos) instanceof DormantFlowerBlockEntity bud) {
                level.setBlockAndUpdate(pos, bud.getFlower());
            }
        } else if (wiltedHere && bloomTime && random.nextDouble() < FlowerRules.BLOOM_CHANCE) {
            BlockState regrown = memory.takeWilted(pos);
            if (regrown != null && regrown.canSurvive(level, pos)) {
                level.setBlockAndUpdate(pos, regrown);
            }
            chunk.markUnsaved();
        }
    }

    /** Remember flowers that players plant (also a wild flower that was picked and replanted). */
    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockPos pos = event.getPos();
        LevelChunk chunk = level.getChunkAt(pos);
        boolean planting = event.getEntity() instanceof Player && isSeasonalFlower(event.getPlacedBlock());
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
            if (memory != null && memory.isPlanted(event.getPos())) {
                memory.unplant(event.getPos());
                chunk.markUnsaved();
            }
        }
    }
}
