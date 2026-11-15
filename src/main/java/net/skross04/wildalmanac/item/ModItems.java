package net.skross04.wildalmanac.item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.item.custom.SeasonCalendarItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WildAlmanac.MODID);

    public static final DeferredItem<SeasonCalendarItem> SEASON_CALENDAR = ITEMS.registerItem("season_calendar",
            SeasonCalendarItem::new, properties -> properties.stacksTo(1));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
