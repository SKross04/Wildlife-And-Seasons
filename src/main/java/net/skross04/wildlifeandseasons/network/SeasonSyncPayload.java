package net.skross04.wildlifeandseasons.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;

/**
 * Server → client message: "the season calendar is at this many ticks", plus the world's weather seed.
 * The client works out the season and day from it, like the server does, and from the seed which days are foggy
 * or windy (so every player sees the same weather).
 */
public record SeasonSyncPayload(long elapsedTicks, long weatherSeed) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SeasonSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(WildlifeAndSeasons.MODID, "season_sync"));

    /** How the message is written to and read from the network: two numbers. */
    public static final StreamCodec<ByteBuf, SeasonSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, SeasonSyncPayload::elapsedTicks,
            ByteBufCodecs.LONG, SeasonSyncPayload::weatherSeed,
            SeasonSyncPayload::new);

    @Override
    public CustomPacketPayload.Type<SeasonSyncPayload> type() {
        return TYPE;
    }
}
