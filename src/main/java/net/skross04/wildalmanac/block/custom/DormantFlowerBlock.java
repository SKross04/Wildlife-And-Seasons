package net.skross04.wildalmanac.block.custom;

import java.util.List;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.skross04.wildalmanac.block.entity.DormantFlowerBlockEntity;

/**
 * A flower a player planted, resting outside its bloom time. It remembers which flower it is
 * (in its block entity) and opens again in spring. Breaking it gives you that flower.
 * <p>
 * {@code VegetationBlock} (called BushBlock in older versions) gives it the flower rules:
 * it needs dirt or grass below and breaks when that's removed.
 */
public class DormantFlowerBlock extends VegetationBlock implements EntityBlock {
    private static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);

    public DormantFlowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DormantFlowerBlockEntity(pos, state);
    }

    /** Drop whatever the sleeping flower would drop (so you get your tulip back). */
    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof DormantFlowerBlockEntity dormant) {
            return dormant.getFlower().getDrops(params);
        }
        return List.of();
    }

    /** Middle-click in creative gives the sleeping flower. */
    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        if (level.getBlockEntity(pos) instanceof DormantFlowerBlockEntity dormant) {
            return new ItemStack(dormant.getFlower().getBlock());
        }
        return ItemStack.EMPTY;
    }
}
