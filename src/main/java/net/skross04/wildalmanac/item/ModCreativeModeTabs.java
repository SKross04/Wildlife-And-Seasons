package net.skross04.wildalmanac.item;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.skross04.wildalmanac.WildAlmanac;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WildAlmanac.MODID);

    public static final String WILD_ALMANAC_TAB_TITLE = "creativetab." + WildAlmanac.MODID + ".wild_almanac";

    public static final Supplier<CreativeModeTab> WILD_ALMANAC_TAB = CREATIVE_MODE_TABS.register("wild_almanac",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SEASON_CALENDAR.get()))
                    .title(Component.translatable(WILD_ALMANAC_TAB_TITLE))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.SEASON_CALENDAR.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
