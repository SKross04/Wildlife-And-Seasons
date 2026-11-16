package net.skross04.wildlifeandseasons.particle;

import java.util.function.Supplier;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;

/** Our particle types: northern lights and shooting stars (only ever made on the client, high in the sky). */
public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, WildlifeAndSeasons.MODID);

    public static final Supplier<SimpleParticleType> AURORA = PARTICLE_TYPES.register("aurora", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> SHOOTING_STAR =
            PARTICLE_TYPES.register("shooting_star", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> SHOOTING_STAR_TRAIL =
            PARTICLE_TYPES.register("shooting_star_trail", () -> new SimpleParticleType(true));

    private ModParticles() {
    }

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
