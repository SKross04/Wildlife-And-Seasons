package net.skross04.wildlifeandseasons.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.item.ModItems;

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

        // A glass tube with red "mercury" (redstone) on a stick.
        shaped(RecipeCategory.TOOLS, ModItems.THERMOMETER.get())
                .pattern("G")
                .pattern("R")
                .pattern("S")
                .define('G', Items.GLASS_PANE)
                .define('R', Items.REDSTONE)
                .define('S', Items.STICK)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(output, recipeKey("thermometer"));

        // Dyes from the early flowers, like vanilla flowers: white from snowdrops, purple from crocuses.
        shapeless(RecipeCategory.MISC, Items.DYE.pick(DyeColor.WHITE))
                .requires(ModItems.SNOWDROP.get())
                .group("white_dye")
                .unlockedBy("has_snowdrop", has(ModItems.SNOWDROP.get()))
                .save(output, recipeKey("white_dye_from_snowdrop"));
        shapeless(RecipeCategory.MISC, Items.DYE.pick(DyeColor.PURPLE))
                .requires(ModItems.CROCUS.get())
                .group("purple_dye")
                .unlockedBy("has_crocus", has(ModItems.CROCUS.get()))
                .save(output, recipeKey("purple_dye_from_crocus"));

        // An open glass cup on a stick.
        shaped(RecipeCategory.REDSTONE, ModItems.RAIN_GAUGE.get())
                .pattern("G G")
                .pattern("GGG")
                .pattern(" S ")
                .define('G', Items.GLASS_PANE)
                .define('S', Items.STICK)
                .unlockedBy("has_glass_pane", has(Items.GLASS_PANE))
                .save(output, recipeKey("rain_gauge"));
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(WildlifeAndSeasons.MODID, path));
    }
}
