package net.skross04.wildlifeandseasons.item;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.block.ModBlocks;
import net.skross04.wildlifeandseasons.item.custom.SeasonCalendarItem;
import net.skross04.wildlifeandseasons.item.custom.ThermometerItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WildlifeAndSeasons.MODID);

    public static final DeferredItem<SeasonCalendarItem> SEASON_CALENDAR = ITEMS.registerItem("season_calendar",
            SeasonCalendarItem::new, properties -> properties.stacksTo(1));

    // Block items: the item you hold and place. Its name comes from the block's translation.
    public static final DeferredItem<BlockItem> ICICLE = ITEMS.registerSimpleBlockItem(ModBlocks.ICICLE);
    public static final DeferredItem<BlockItem> RAIN_GAUGE = ITEMS.registerSimpleBlockItem(ModBlocks.RAIN_GAUGE);
    public static final DeferredItem<BlockItem> SNOWDROP = ITEMS.registerSimpleBlockItem(ModBlocks.SNOWDROP);
    public static final DeferredItem<BlockItem> CROCUS = ITEMS.registerSimpleBlockItem(ModBlocks.CROCUS);
    public static final DeferredItem<BlockItem> DESERT_SUNFLOWER = ITEMS.registerSimpleBlockItem(ModBlocks.DESERT_SUNFLOWER);
    public static final DeferredItem<BlockItem> SAND_VERBENA = ITEMS.registerSimpleBlockItem(ModBlocks.SAND_VERBENA);
    public static final DeferredItem<BlockItem> DUNE_PRIMROSE = ITEMS.registerSimpleBlockItem(ModBlocks.DUNE_PRIMROSE);
    public static final DeferredItem<BlockItem> LICHEN = ITEMS.registerSimpleBlockItem(ModBlocks.LICHEN);

    public static final DeferredItem<ThermometerItem> THERMOMETER = ITEMS.registerItem("thermometer",
            ThermometerItem::new, properties -> properties.stacksTo(1));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
