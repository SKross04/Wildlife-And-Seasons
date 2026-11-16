package net.skross04.wildlifeandseasons.block;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.block.custom.DesertFlowerBlock;
import net.skross04.wildlifeandseasons.block.custom.DormantFlowerBlock;
import net.skross04.wildlifeandseasons.block.custom.IcicleBlock;
import net.skross04.wildlifeandseasons.block.custom.RainGaugeBlock;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WildlifeAndSeasons.MODID);

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

    /**
     * Snowdrop: the first flower of the year, nodding white bells in late winter (real snowdrops flower January–March,
     * often before the last snow has melted). Woods and riverbanks.
     */
    public static final DeferredBlock<FlowerBlock> SNOWDROP = BLOCKS.registerBlock("snowdrop",
            properties -> new FlowerBlock(MobEffects.WEAKNESS, 7, properties), ModBlocks::flowerProperties);

    /** Crocus: purple cups at the very start of spring (real crocuses flower February–April). Meadows and plains. */
    public static final DeferredBlock<FlowerBlock> CROCUS = BLOCKS.registerBlock("crocus",
            properties -> new FlowerBlock(MobEffects.NAUSEA, 7, properties), ModBlocks::flowerProperties);

    public static final DeferredBlock<FlowerPotBlock> POTTED_SNOWDROP = BLOCKS.registerBlock("potted_snowdrop",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SNOWDROP, properties),
            ModBlocks::pottedProperties);
    public static final DeferredBlock<FlowerPotBlock> POTTED_CROCUS = BLOCKS.registerBlock("potted_crocus",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CROCUS, properties),
            ModBlocks::pottedProperties);

    /*
     * The desert bloom (real example: Anza-Borrego, California): wildflowers that come up on the sand after the
     * rare desert rains. Wild ones die off again; placed ones stay (see DesertFlowerBlock).
     */
    /** Desert sunflower (Geraea canescens, "desert gold"): yellow daisies on hairy grey-green stems. */
    public static final DeferredBlock<DesertFlowerBlock> DESERT_SUNFLOWER = BLOCKS.registerBlock("desert_sunflower",
            properties -> new DesertFlowerBlock(MobEffects.FIRE_RESISTANCE, 4, properties), ModBlocks::flowerProperties);

    /** Desert sand verbena (Abronia villosa): low, sprawling, with round magenta-pink clusters. */
    public static final DeferredBlock<DesertFlowerBlock> SAND_VERBENA = BLOCKS.registerBlock("sand_verbena",
            properties -> new DesertFlowerBlock(MobEffects.REGENERATION, 8, properties), ModBlocks::flowerProperties);

    /** Dune evening primrose (Oenothera deltoides): big white flowers that open in the evening (night vision). */
    public static final DeferredBlock<DesertFlowerBlock> DUNE_PRIMROSE = BLOCKS.registerBlock("dune_primrose",
            properties -> new DesertFlowerBlock(MobEffects.NIGHT_VISION, 5, properties), ModBlocks::flowerProperties);

    public static final DeferredBlock<FlowerPotBlock> POTTED_DESERT_SUNFLOWER = BLOCKS.registerBlock("potted_desert_sunflower",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DESERT_SUNFLOWER, properties),
            ModBlocks::pottedProperties);
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAND_VERBENA = BLOCKS.registerBlock("potted_sand_verbena",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAND_VERBENA, properties),
            ModBlocks::pottedProperties);
    public static final DeferredBlock<FlowerPotBlock> POTTED_DUNE_PRIMROSE = BLOCKS.registerBlock("potted_dune_primrose",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DUNE_PRIMROSE, properties),
            ModBlocks::pottedProperties);

    /**
     * Lichen: crusty grey-green and yellow-green patches (like real foliose and map lichens) on open natural rock.
     * Grows slowly over the years (see MossAndLichen); a multiface block like glow lichen, but without light.
     */
    public static final DeferredBlock<MultifaceBlock> LICHEN = BLOCKS.registerBlock("lichen", MultifaceBlock::new,
            properties -> properties
                    .mapColor(MapColor.GLOW_LICHEN)
                    .replaceable()
                    .noCollision()
                    .strength(0.2F)
                    .sound(SoundType.GLOW_LICHEN)
                    .ignitedByLava()
                    .pushReaction(PushReaction.POPPED));

    /** Like vanilla small flowers. */
    private static BlockBehaviour.Properties flowerProperties(BlockBehaviour.Properties properties) {
        return properties
                .mapColor(MapColor.PLANT)
                .noCollision()
                .instabreak()
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .pushReaction(PushReaction.POPPED);
    }

    /** Like vanilla potted flowers. */
    private static BlockBehaviour.Properties pottedProperties(BlockBehaviour.Properties properties) {
        return properties.instabreak().noOcclusion().pushReaction(PushReaction.POPPED);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
