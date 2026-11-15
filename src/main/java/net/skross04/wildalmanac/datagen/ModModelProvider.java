package net.skross04.wildalmanac.datagen;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.block.ModBlocks;
import net.skross04.wildalmanac.item.ModItems;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, WildAlmanac.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // A flat 2D item using textures/item/season_calendar.png
        itemModels.generateFlatItem(ModItems.SEASON_CALENDAR.get(), ModelTemplates.FLAT_ITEM);

        // A cross-shaped plant model (two crossed planes, like flowers), not tinted, using textures/block/dormant_flower.png
        blockModels.createCrossBlock(ModBlocks.DORMANT_FLOWER.get(), BlockModelGenerators.PlantType.NOT_TINTED);
    }
}
