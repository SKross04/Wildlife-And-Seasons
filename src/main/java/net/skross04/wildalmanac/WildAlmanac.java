package net.skross04.wildalmanac;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.skross04.wildalmanac.item.ModCreativeModeTabs;
import net.skross04.wildalmanac.item.ModItems;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(WildAlmanac.MODID)
public class WildAlmanac {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "wildalmanac";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public WildAlmanac(IEventBus modEventBus, ModContainer modContainer) {
        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SYNCED, Config.SPEC);
    }
}
