package net.skross04.wildlifeandseasons.nature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Flower memory for one chunk, saved with the chunk:
 * <ul>
 *     <li>{@code wilted}: wild flowers that wilted after their bloom time, and where (they regrow there in spring),</li>
 *     <li>{@code planted}: positions where a player planted a flower (those become dormant buds instead of disappearing),</li>
 *     <li>{@code mushrooms}: autumn mushrooms (and shelf mushrooms) the seasons grew here; only these rot and freeze
 *     away again, so worldgen and player mushrooms stay.</li>
 * </ul>
 */
public final class ChunkFlowers {
    private record Wilted(BlockPos pos, BlockState flower) {
        static final Codec<Wilted> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(Wilted::pos),
                BlockState.CODEC.fieldOf("flower").forGetter(Wilted::flower)
        ).apply(instance, Wilted::new));
    }

    public static final MapCodec<ChunkFlowers> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Wilted.CODEC.listOf().optionalFieldOf("wilted", List.of()).forGetter(ChunkFlowers::wiltedList),
            BlockPos.CODEC.listOf().optionalFieldOf("planted", List.of()).forGetter(flowers -> List.copyOf(flowers.planted)),
            BlockPos.CODEC.listOf().optionalFieldOf("mushrooms", List.of()).forGetter(flowers -> List.copyOf(flowers.mushrooms))
    ).apply(instance, ChunkFlowers::new));

    private final Map<BlockPos, BlockState> wilted = new HashMap<>();
    private final Set<BlockPos> planted = new HashSet<>();
    private final Set<BlockPos> mushrooms = new HashSet<>();

    public ChunkFlowers() {
    }

    private ChunkFlowers(List<Wilted> wiltedList, List<BlockPos> plantedList, List<BlockPos> mushroomList) {
        wiltedList.forEach(entry -> wilted.put(entry.pos(), entry.flower()));
        planted.addAll(plantedList);
        mushrooms.addAll(mushroomList);
    }

    private List<Wilted> wiltedList() {
        return wilted.entrySet().stream().map(entry -> new Wilted(entry.getKey(), entry.getValue())).toList();
    }

    public void addWilted(BlockPos pos, BlockState flower) {
        wilted.put(pos.immutable(), flower);
    }

    public boolean hasWilted(BlockPos pos) {
        return wilted.containsKey(pos);
    }

    /** All spots where a wild flower wilted and waits to come back. */
    public List<BlockPos> wiltedPositions() {
        return List.copyOf(wilted.keySet());
    }

    /** The remembered flower at this spot, without forgetting it. */
    public @Nullable BlockState peekWilted(BlockPos pos) {
        return wilted.get(pos);
    }

    /** Takes the remembered flower out (it's about to regrow, or the spot is no longer free). */
    public @Nullable BlockState takeWilted(BlockPos pos) {
        return wilted.remove(pos);
    }

    public void plant(BlockPos pos) {
        planted.add(pos.immutable());
    }

    public void unplant(BlockPos pos) {
        planted.remove(pos);
    }

    public boolean isPlanted(BlockPos pos) {
        return planted.contains(pos);
    }

    public void addMushroom(BlockPos pos) {
        mushrooms.add(pos.immutable());
    }

    public void removeMushroom(BlockPos pos) {
        mushrooms.remove(pos);
    }

    /** True if the seasons grew a mushroom here (so it may rot or freeze away again). */
    public boolean isSeasonalMushroom(BlockPos pos) {
        return mushrooms.contains(pos);
    }

    public boolean hasMushrooms() {
        return !mushrooms.isEmpty();
    }

    public List<BlockPos> mushroomPositions() {
        return List.copyOf(mushrooms);
    }
}
