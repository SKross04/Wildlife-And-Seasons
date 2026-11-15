package net.skross04.wildlifeandseasons.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * A block entity is extra data stored with one block in the world. This one remembers which flower
 * (with all its details, like the direction of pink petals) is sleeping in a dormant flower.
 */
public class DormantFlowerBlockEntity extends BlockEntity {
    private BlockState flower = Blocks.DANDELION.defaultBlockState();

    public DormantFlowerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DORMANT_FLOWER.get(), pos, state);
    }

    public BlockState getFlower() {
        return flower;
    }

    public void setFlower(BlockState flower) {
        this.flower = flower;
        setChanged(); // tells Minecraft this chunk needs saving
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("flower", BlockState.CODEC, flower);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read("flower", BlockState.CODEC).ifPresent(state -> flower = state);
    }
}
