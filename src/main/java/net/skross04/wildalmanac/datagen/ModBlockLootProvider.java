package net.skross04.wildalmanac.datagen;

import java.util.Set;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.skross04.wildalmanac.block.ModBlocks;

/** What each block drops when broken (its loot table). Every Wild Almanac block needs a line here. */
public class ModBlockLootProvider extends BlockLootSubProvider {
    public ModBlockLootProvider(LootTableSubProvider.Context context) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), context);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.RAIN_GAUGE.get());
        add(ModBlocks.ICICLE.get(), createSilkTouchOnlyTable(ModBlocks.ICICLE.get())); // shatters, unless silk touch
        add(ModBlocks.DORMANT_FLOWER.get(), noDrop());  // drops its flower through DormantFlowerBlock#getDrops
    }

    /** Only check our own blocks (otherwise it would expect tables for every vanilla block). */
    @Override
    public Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().<Block>map(holder -> holder.value()).toList();
    }
}
