package net.skross04.wildalmanac.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.item.ModItems;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        super(recipes, advancements);
    }

    @Override
    protected void buildRecipes() {
        // Cheap and early-game: three sheets of paper hung on a stick.
        shapeless(RecipeCategory.TOOLS, ModItems.SEASON_CALENDAR.get())
                .requires(Items.PAPER)
                .requires(Items.PAPER)
                .requires(Items.PAPER)
                .requires(Items.STICK)
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(output, recipeKey("season_calendar"));
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(WildAlmanac.MODID, path));
    }
}
