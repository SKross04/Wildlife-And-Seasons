package net.skross04.wildalmanac.block.entity;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.block.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, WildAlmanac.MODID);

    public static final Supplier<BlockEntityType<DormantFlowerBlockEntity>> DORMANT_FLOWER = BLOCK_ENTITIES.register(
            "dormant_flower", () -> new BlockEntityType<>(DormantFlowerBlockEntity::new, ModBlocks.DORMANT_FLOWER.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
