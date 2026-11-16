package net.skross04.wildlifeandseasons.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.ImpossibleTrigger;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.advancement.SeasonAdvancements;
import net.skross04.wildlifeandseasons.block.ModBlocks;
import net.skross04.wildlifeandseasons.item.ModItems;
import net.skross04.wildlifeandseasons.nature.MossAndLichen;

/**
 * The "Wildlife & Seasons" advancement tab (Design Plan §10: fun advancements for watching nature).
 * Ones that depend on the season or weather use an impossible criterion "done" that {@link SeasonAdvancements}
 * grants from code; the others use vanilla triggers. Since 26.3 advancements are a data registry, so this is a
 * registry bootstrap like world generation.
 */
public final class ModAdvancements {
    private ModAdvancements() {
    }

    public static void bootstrap(BootstrapContext<Advancement> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);

        AdvancementHolder root = Advancement.Builder.advancement()
                .rootDisplay(ModItems.SEASON_CALENDAR.get(), title("root"), description("root"),
                        Identifier.withDefaultNamespace("gui/advancements/backgrounds/husbandry"),
                        AdvancementType.TASK, false, false, false)
                .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                .save(context, id("root"));

        AdvancementHolder calendar = task(ModItems.SEASON_CALENDAR.get(), "know_the_season", AdvancementType.TASK, root)
                .addCriterion("calendar", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SEASON_CALENDAR.get()))
                .save(context, id("know_the_season"));
        AdvancementHolder snow = granted(Items.SNOWBALL, SeasonAdvancements.SNOWED_IN, AdvancementType.TASK, calendar, context);
        granted(ModItems.SEASON_CALENDAR.get(), SeasonAdvancements.YEAR_IN_THE_WILD, AdvancementType.CHALLENGE, snow, context);
        granted(Items.LEAF_LITTER, SeasonAdvancements.LEAF_PEEPER, AdvancementType.TASK, calendar, context);
        granted(Items.PINK_PETALS, SeasonAdvancements.HANAMI, AdvancementType.TASK, calendar, context);
        granted(Items.WHEAT, SeasonAdvancements.WINTER_HARVEST, AdvancementType.GOAL, calendar, context);

        AdvancementHolder spring = task(ModItems.SNOWDROP.get(), "first_signs_of_spring", AdvancementType.TASK, root)
                .addCriterion("snowdrop", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SNOWDROP.get()))
                .addCriterion("crocus", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.CROCUS.get()))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(context, id("first_signs_of_spring"));
        task(ModItems.DESERT_SUNFLOWER.get(), "superbloom", AdvancementType.GOAL, spring)
                .addCriterion("desert_sunflower", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.DESERT_SUNFLOWER.get()))
                .addCriterion("sand_verbena", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SAND_VERBENA.get()))
                .addCriterion("dune_primrose", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.DUNE_PRIMROSE.get()))
                .save(context, id("superbloom"));

        granted(Items.RED_MUSHROOM, SeasonAdvancements.MUSHROOM_SEASON, AdvancementType.TASK, root, context);
        task(Items.MOSSY_COBBLESTONE, "scraped_clean", AdvancementType.TASK, root)
                .addCriterion("scrape", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location().setBlock(
                                BlockPredicate.Builder.block().of(blocks, MossAndLichen.MOSSY.values())),
                        ItemPredicate.Builder.item().of(items, ItemTags.AXES)))
                .save(context, id("scraped_clean"));
        AdvancementHolder brr = granted(ModItems.THERMOMETER.get(), SeasonAdvancements.BRR, AdvancementType.TASK, root, context);
        task(ModItems.RAIN_GAUGE.get(), "rain_check", AdvancementType.TASK, brr)
                .addCriterion("placed", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, ModBlocks.RAIN_GAUGE.get()))
                .save(context, id("rain_check"));
    }

    private static Advancement.Builder task(Item icon, String name, AdvancementType type, AdvancementHolder parent) {
        return Advancement.Builder.advancement()
                .parent(parent)
                .display(icon, title(name), description(name), type, true, true, false);
    }

    /** An advancement the mod grants from code (impossible criterion "done"). */
    private static AdvancementHolder granted(Item icon, String name, AdvancementType type, AdvancementHolder parent,
                                             BootstrapContext<Advancement> context) {
        Criterion<ImpossibleTrigger.TriggerInstance> done =
                CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance());
        return task(icon, name, type, parent).addCriterion(SeasonAdvancements.CRITERION, done).save(context, id(name));
    }

    private static String id(String name) {
        return WildlifeAndSeasons.MODID + ":" + SeasonAdvancements.FOLDER + name;
    }

    public static String titleKey(String name) {
        return "advancements." + WildlifeAndSeasons.MODID + "." + name + ".title";
    }

    public static String descriptionKey(String name) {
        return "advancements." + WildlifeAndSeasons.MODID + "." + name + ".description";
    }

    private static Component title(String name) {
        return Component.translatable(titleKey(name));
    }

    private static Component description(String name) {
        return Component.translatable(descriptionKey(name));
    }
}
