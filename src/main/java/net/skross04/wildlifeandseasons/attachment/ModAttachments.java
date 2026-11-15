package net.skross04.wildlifeandseasons.attachment;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.nature.ChunkFlowers;

/**
 * Data attachments: NeoForge's way to store extra data on chunks, entities or worlds,
 * saved and loaded together with them.
 */
public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, WildlifeAndSeasons.MODID);

    /** Which wild flowers wilted where, and which flowers players planted (per chunk). */
    public static final Supplier<AttachmentType<ChunkFlowers>> CHUNK_FLOWERS = ATTACHMENT_TYPES.register("chunk_flowers",
            () -> AttachmentType.builder(() -> new ChunkFlowers()).serialize(ChunkFlowers.MAP_CODEC).build());

    /**
     * The calendar day a chunk was last up to date with the seasons (-1 = not known yet).
     * Chunks nobody was near catch up when they load again (see {@code ChunkCatchUp}).
     */
    public static final Supplier<AttachmentType<Long>> SEASON_DAY = ATTACHMENT_TYPES.register("season_day",
            () -> AttachmentType.builder(() -> -1L).serialize(Codec.LONG.fieldOf("day")).build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
