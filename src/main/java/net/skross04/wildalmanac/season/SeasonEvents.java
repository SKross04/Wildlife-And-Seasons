package net.skross04.wildalmanac.season;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.climate.BiomeZones;
import net.skross04.wildalmanac.climate.ClimateSnapshot;
import net.skross04.wildalmanac.command.SeasonCommand;
import net.skross04.wildalmanac.ecology.season.SeasonDate;
import net.skross04.wildalmanac.network.ModNetworking;

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
        SeasonSavedData data = SeasonSavedData.get(server);
        data.tick(WorldSeasons.overworldClockTicks(server));
        // For code without a world at hand (the Biome mixin): where we are in the year and the day.
        ClimateSnapshot.updateServer(data.calendar().yearPosition(SeasonDate.DEFAULT_YEAR_LENGTH),
                ClimateSnapshot.dayPosition(server.overworld()));
        if (data.needsSync()) {
            ModNetworking.syncSeasonToAll(server); // once per day, players learn the new date
        }
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

    /** A player joining gets the current season right away. */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ModNetworking.syncSeason(player);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        ClimateSnapshot.clearServer();
        BiomeZones.clear();
    }

    /** Datapacks can move biomes between climate zones, so forget the cached zones when tags reload. */
    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        BiomeZones.clear();
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SeasonCommand.register(event.getDispatcher());
    }
}
