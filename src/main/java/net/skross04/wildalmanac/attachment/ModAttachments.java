package net.skross04.wildalmanac.attachment;

import java.util.function.Supplier;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.nature.ChunkFlowers;

/**
 * Data attachments: NeoForge's way to store extra data on chunks, entities or worlds,
 * saved and loaded together with them.
 */
public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, WildAlmanac.MODID);

    /** Which wild flowers wilted where, and which flowers players planted (per chunk). */
    public static final Supplier<AttachmentType<ChunkFlowers>> CHUNK_FLOWERS = ATTACHMENT_TYPES.register("chunk_flowers",
            () -> AttachmentType.builder(() -> new ChunkFlowers()).serialize(ChunkFlowers.MAP_CODEC).build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
