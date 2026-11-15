package net.skross04.wildalmanac.datagen;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.block.ModBlocks;
import net.skross04.wildalmanac.block.custom.RainGaugeBlock;
import net.skross04.wildalmanac.item.ModItems;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, WildAlmanac.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // A flat 2D item using textures/item/season_calendar.png
        itemModels.generateFlatItem(ModItems.SEASON_CALENDAR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.THERMOMETER.get(), ModelTemplates.FLAT_ITEM);

        // A cross-shaped plant model (two crossed planes, like flowers), not tinted, using textures/block/dormant_flower.png
        blockModels.createCrossBlock(ModBlocks.DORMANT_FLOWER.get(), BlockModelGenerators.PlantType.NOT_TINTED);
        // The icicle item is a flat sprite of the same texture (like saplings and flowers).
        blockModels.createCrossBlockWithDefaultItem(ModBlocks.ICICLE.get(), BlockModelGenerators.PlantType.NOT_TINTED);

        // The rain gauge has hand-made 3D models (models/block/rain_gauge_*.json in src/main/resources):
        // one per water level, so you see the water rise behind the glass. The item shows the empty one.
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.RAIN_GAUGE.get())
                .with(PropertyDispatch.initial(RainGaugeBlock.FILL).generate(fill -> BlockModelGenerators.plainVariant(
                        Identifier.fromNamespaceAndPath(WildAlmanac.MODID, "block/rain_gauge_" + waterLevel(fill))))));
        itemModels.itemModelOutput.accept(ModItems.RAIN_GAUGE.get(), ItemModelUtils.plainModel(
                Identifier.fromNamespaceAndPath(WildAlmanac.MODID, "block/rain_gauge_empty")));
    }

    /** Fill 0–15 → which of the four water models. */
    private static String waterLevel(int fill) {
        if (fill == 0) {
            return "empty";
        }
        return fill <= 5 ? "low" : fill <= 10 ? "half" : "full";
    }
}
