package net.skross04.wildlifeandseasons.item;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WildlifeAndSeasons.MODID);

    public static final String WILDLIFE_AND_SEASONS_TAB_TITLE = "creativetab." + WildlifeAndSeasons.MODID + ".wildlife_and_seasons";

    public static final Supplier<CreativeModeTab> WILDLIFE_AND_SEASONS_TAB = CREATIVE_MODE_TABS.register("wildlife_and_seasons",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SEASON_CALENDAR.get()))
                    .title(Component.translatable(WILDLIFE_AND_SEASONS_TAB_TITLE))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.SEASON_CALENDAR.get());
                        output.accept(ModItems.THERMOMETER.get());
                        output.accept(ModItems.RAIN_GAUGE.get());
                        output.accept(ModItems.ICICLE.get());
                        output.accept(ModItems.SNOWDROP.get());
                        output.accept(ModItems.CROCUS.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
