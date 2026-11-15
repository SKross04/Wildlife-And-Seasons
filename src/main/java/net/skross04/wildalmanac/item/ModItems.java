package net.skross04.wildalmanac.item;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.block.ModBlocks;
import net.skross04.wildalmanac.item.custom.SeasonCalendarItem;
import net.skross04.wildalmanac.item.custom.ThermometerItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WildAlmanac.MODID);

    public static final DeferredItem<SeasonCalendarItem> SEASON_CALENDAR = ITEMS.registerItem("season_calendar",
            SeasonCalendarItem::new, properties -> properties.stacksTo(1));

    // Block items: the item you hold and place. Its name comes from the block's translation.
    public static final DeferredItem<BlockItem> ICICLE = ITEMS.registerSimpleBlockItem(ModBlocks.ICICLE);
    public static final DeferredItem<BlockItem> RAIN_GAUGE = ITEMS.registerSimpleBlockItem(ModBlocks.RAIN_GAUGE);

    public static final DeferredItem<ThermometerItem> THERMOMETER = ITEMS.registerItem("thermometer",
            ThermometerItem::new, properties -> properties.stacksTo(1));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
