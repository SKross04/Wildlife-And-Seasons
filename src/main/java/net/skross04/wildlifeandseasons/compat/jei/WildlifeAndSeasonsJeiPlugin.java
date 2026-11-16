package net.skross04.wildlifeandseasons.compat.jei;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.compat.CompatTranslations;
import net.skross04.wildlifeandseasons.ecology.season.Season;
import net.skross04.wildlifeandseasons.farming.CropSeasons;
import net.skross04.wildlifeandseasons.item.ModItems;

/**
 * JEI (optional): info pages for our items, and for every crop the seasons it grows in.
 * JEI finds this class by its {@link JeiPlugin} annotation; without JEI it's never loaded. EMI and REI show
 * these pages too through their JEI support.
 */
@JeiPlugin
public class WildlifeAndSeasonsJeiPlugin implements IModPlugin {
    private static final Identifier UID = Identifier.fromNamespaceAndPath(WildlifeAndSeasons.MODID, "jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(ModItems.SEASON_CALENDAR.get(), Component.translatable(CompatTranslations.JEI_CALENDAR));
        registration.addIngredientInfo(ModItems.THERMOMETER.get(), Component.translatable(CompatTranslations.JEI_THERMOMETER));
        registration.addIngredientInfo(ModItems.RAIN_GAUGE.get(), Component.translatable(CompatTranslations.JEI_RAIN_GAUGE));
        registration.addIngredientInfo(ModItems.ICICLE.get(), Component.translatable(CompatTranslations.JEI_ICICLE));

        // Crops: the seasons come from the block tags (so datapacks and modded crops are included).
        Set<Item> done = new HashSet<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            EnumSet<Season> seasons = CropSeasons.seasonsFor(block.defaultBlockState());
            Item item = block.asItem();
            if (seasons == null || item == Items.AIR || !done.add(item)) {
                continue;
            }
            registration.addIngredientInfo(item, CompatTranslations.growsIn(seasons),
                    Component.translatable(CompatTranslations.JEI_CROP_ALL_YEAR));
        }
    }
}
