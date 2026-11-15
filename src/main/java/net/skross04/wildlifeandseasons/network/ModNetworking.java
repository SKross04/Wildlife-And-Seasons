package net.skross04.wildlifeandseasons.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.season.SeasonAccess;
import net.skross04.wildlifeandseasons.season.SeasonSavedData;

@EventBusSubscriber(modid = WildlifeAndSeasons.MODID)
public class ModNetworking {
    /** Bump this when a payload changes, so old and new versions of the mod don't talk past each other. */
    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
                .playToClient(SeasonSyncPayload.TYPE, SeasonSyncPayload.STREAM_CODEC,
                        (payload, context) -> SeasonAccess.setClientElapsedTicks(payload.elapsedTicks()));
    }

    /** Sends the current season to one player (e.g. when they join). */
    public static void syncSeason(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new SeasonSyncPayload(SeasonSavedData.get(player.level().getServer()).calendar().elapsedTicks()));
    }

    /** Sends the current season to everyone (new day, or /season set). */
    public static void syncSeasonToAll(MinecraftServer server) {
        SeasonSavedData data = SeasonSavedData.get(server);
        data.markSynced();
        PacketDistributor.sendToAllPlayers(new SeasonSyncPayload(data.calendar().elapsedTicks()));
    }
}
