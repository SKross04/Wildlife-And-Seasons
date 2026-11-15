package net.skross04.wildalmanac.season;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.command.SeasonCommand;

/**
 * Keeps the season calendar running. {@code @EventBusSubscriber} makes NeoForge call these
 * methods automatically when the events happen.
 */
@EventBusSubscriber(modid = WildAlmanac.MODID)
public class SeasonEvents {
    /** Once per server tick, after the world has ticked: let the calendar follow the day clock. */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        SeasonSavedData.get(server).tick(WorldSeasons.overworldClockTicks(server));
    }

    /**
     * Fired when everyone sleeps and the night is about to be skipped. LOWEST priority runs us last,
     * so if another mod cancels the skip we never hear about it.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        if (event.getLevel() instanceof ServerLevel level && level == level.getServer().overworld()) {
            SeasonSavedData.get(level.getServer()).markSlept();
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SeasonCommand.register(event.getDispatcher());
    }
}
