package net.skross04.wildalmanac.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.block.custom.DormantFlowerBlock;
import net.skross04.wildalmanac.block.custom.IcicleBlock;
import net.skross04.wildalmanac.block.custom.RainGaugeBlock;

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

    /**
     * An icicle under a snowy roof edge in winter. Breaks easily; only silk touch keeps it.
     * Icicles a player places never melt (see {@link IcicleBlock#PERSISTENT}).
     */
    public static final DeferredBlock<IcicleBlock> ICICLE = BLOCKS.registerBlock("icicle",
            IcicleBlock::new, properties -> properties
                    .mapColor(MapColor.ICE)
                    .noCollision()
                    .noOcclusion()
                    .strength(0.1F)
                    .sound(SoundType.GLASS)
                    .randomTicks()
                    .pushReaction(PushReaction.POPPED));

    /** A rain gauge that fills when it rains; comparator gives how full it is. */
    public static final DeferredBlock<RainGaugeBlock> RAIN_GAUGE = BLOCKS.registerBlock("rain_gauge",
            RainGaugeBlock::new, properties -> properties
                    .mapColor(MapColor.NONE)
                    .noOcclusion()
                    .strength(0.3F)
                    .sound(SoundType.GLASS)
                    .pushReaction(PushReaction.POPPED));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
