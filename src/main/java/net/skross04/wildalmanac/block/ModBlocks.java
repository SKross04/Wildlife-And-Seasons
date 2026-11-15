package net.skross04.wildalmanac.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.block.custom.DormantFlowerBlock;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WildAlmanac.MODID);

    /**
     * A planted flower outside its bloom time: a green stem with a closed bud. No item; it only appears in the world.
     * Like a flower: no collision, breaks instantly, pops off when a piston pushes it.
     */
    public static final DeferredBlock<DormantFlowerBlock> DORMANT_FLOWER = BLOCKS.registerBlock("dormant_flower",
            DormantFlowerBlock::new, properties -> properties
                    .mapColor(MapColor.PLANT)
                    .noCollision()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.POPPED));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
