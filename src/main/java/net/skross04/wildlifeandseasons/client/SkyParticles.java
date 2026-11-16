package net.skross04.wildlifeandseasons.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.particle.ModParticles;

/** The particles of the night sky: aurora curtains and shooting stars. */
@EventBusSubscriber(modid = WildlifeAndSeasons.MODID, value = Dist.CLIENT)
public final class SkyParticles {
    /** Full brightness: they glow by themselves. */
    private static final int FULL_BRIGHT = 0xF000F0;

    private SkyParticles() {
    }

    @SubscribeEvent
    public static void registerProviders(RegisterParticleProvidersEvent event) {
        // The three numbers usually used for speed carry the colour for the aurora, the speed for shooting stars.
        event.registerSpriteSet(ModParticles.AURORA.get(), sprites -> (type, level, x, y, z, r, g, b, random) ->
                new Aurora(level, x, y, z, (float) r, (float) g, (float) b, sprites));
        event.registerSpriteSet(ModParticles.SHOOTING_STAR.get(), sprites -> (type, level, x, y, z, dx, dy, dz, random) ->
                new ShootingStar(level, x, y, z, dx, dy, dz, sprites, false));
        event.registerSpriteSet(ModParticles.SHOOTING_STAR_TRAIL.get(), sprites -> (type, level, x, y, z, dx, dy, dz, random) ->
                new ShootingStar(level, x, y, z, 0, 0, 0, sprites, true));
    }

    /** One tall glowing streak of an aurora curtain: fades in, shimmers, fades out; always faces you upright. */
    static final class Aurora extends SingleQuadParticle {
        private final float peak;

        Aurora(ClientLevel level, double x, double y, double z, float r, float g, float b, SpriteSet sprites) {
            super(level, x, y, z, sprites.get(level.getRandom()));
            setColor(r, g, b);
            this.quadSize = 8 + this.random.nextFloat() * 6;
            this.lifetime = 140 + this.random.nextInt(160);
            this.hasPhysics = false;
            this.gravity = 0;
            this.xd = (this.random.nextFloat() - 0.5) * 0.04;
            this.yd = 0;
            this.zd = 0;
            this.peak = (float) Math.min(0.75, SkyShow.auroraBrightness() * (0.6 + 0.4 * this.random.nextFloat()));
            setAlpha(0);
        }

        @Override
        public void tick() {
            super.tick();
            float life = (float) this.age / this.lifetime;
            float fade = (float) Math.sin(Math.PI * life);
            float shimmer = 0.85F + 0.15F * (float) Math.sin((this.age + this.x) * 0.3);
            setAlpha(this.peak * fade * shimmer);
        }

        @Override
        public FacingCameraMode getFacingCameraMode() {
            return FacingCameraMode.LOOKAT_Y;
        }

        @Override
        protected Layer getLayer() {
            return Layer.TRANSLUCENT;
        }

        @Override
        public int getLightCoords(float partialTick) {
            return FULL_BRIGHT;
        }
    }

    /** A shooting star: a bright point racing across the sky, leaving a short fading trail. */
    static final class ShootingStar extends SingleQuadParticle {
        private final boolean trail;

        ShootingStar(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites,
                     boolean trail) {
            super(level, x, y, z, sprites.first());
            this.trail = trail;
            this.hasPhysics = false;
            this.gravity = 0;
            this.friction = 1;
            this.xd = dx;
            this.yd = dy;
            this.zd = dz;
            this.quadSize = trail ? 0.9F : 1.4F;
            this.lifetime = trail ? 10 : 12 + this.random.nextInt(10);
            setColor(1.0F, 0.97F, trail ? 0.85F : 0.92F);
            setAlpha(trail ? 0.8F : 1.0F);
        }

        @Override
        public void tick() {
            double fromX = this.x, fromY = this.y, fromZ = this.z;
            super.tick();
            float life = (float) this.age / this.lifetime;
            if (this.trail) {
                setAlpha(0.8F * (1 - life));
                this.quadSize *= 0.92F;
            } else if (isAlive()) {
                // fade in quickly, burn bright, fade at the end; leave a trail between the old and new place
                setAlpha(Math.min(1, life * 6) * (life > 0.8F ? (1 - life) * 5 : 1));
                for (int i = 1; i <= 3; i++) {
                    double t = i / 3.0;
                    Minecraft.getInstance().particleEngine.createParticle(ModParticles.SHOOTING_STAR_TRAIL.get(),
                            fromX + (this.x - fromX) * t, fromY + (this.y - fromY) * t, fromZ + (this.z - fromZ) * t, 0, 0, 0);
                }
            }
        }

        @Override
        protected Layer getLayer() {
            return Layer.TRANSLUCENT;
        }

        @Override
        public int getLightCoords(float partialTick) {
            return FULL_BRIGHT;
        }
    }

}
