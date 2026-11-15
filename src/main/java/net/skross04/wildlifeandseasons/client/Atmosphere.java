package net.skross04.wildlifeandseasons.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.skross04.wildlifeandseasons.ClientConfig;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.climate.WorldClimate;

/**
 * Small seasonal details on the player's screen: breath clouds in the cold, snow falling from trees,
 * and how windy it is in the trees. Each has its own switch in the client config.
 */
@EventBusSubscriber(modid = WildlifeAndSeasons.MODID, value = Dist.CLIENT)
public final class Atmosphere {
    /** °C: at or below this, breath turns into little clouds. */
    private static final double BREATH_TEMPERATURE = 3.0;
    /** Ticks between two breaths of one creature (3 seconds). */
    private static final int BREATH_INTERVAL = 60;
    private static final double BREATH_DISTANCE = 32.0;

    private Atmosphere() {
    }

    /** Breath clouds: every few seconds, each nearby player, animal and villager breathes out a little cloud. */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.player == null || minecraft.isPaused() || !ClientConfig.breathInCold()) {
            return;
        }
        long time = level.getGameTime();
        Vec3 viewer = minecraft.player.position();
        for (Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof Player || entity instanceof Animal || entity instanceof AbstractVillager)) {
                continue; // undead, fish, golems… don't breathe
            }
            if ((time + entity.getId() * 37L) % BREATH_INTERVAL != 0) {
                continue; // each creature breathes at its own moment
            }
            if (entity.distanceToSqr(viewer) > BREATH_DISTANCE * BREATH_DISTANCE || entity.isInWater() || entity.isInvisible()) {
                continue;
            }
            Vec3 eyes = entity.getEyePosition();
            BlockPos pos = BlockPos.containing(eyes);
            double temperature = WorldClimate.temperatureNow(level.getBiome(pos).value(), pos);
            if (Double.isNaN(temperature) || temperature > BREATH_TEMPERATURE) {
                continue;
            }
            boolean firstPerson = entity == minecraft.player && minecraft.options.getCameraType().isFirstPerson();
            Vec3 look = entity.getLookAngle();
            double forward = firstPerson ? 0.7 : entity.getBbWidth() * 0.6 + 0.1;
            Vec3 mouth = eyes.add(look.scale(forward)).add(0, firstPerson ? -0.3 : -0.1, 0);
            level.addParticle(ParticleTypes.POOF, mouth.x, mouth.y, mouth.z, look.x * 0.02, 0.01, look.z * 0.02);
        }
    }

    /**
     * Called from the leaves' animation tick (see {@code FallingLeavesMixin}): now and then a clump of snow falls
     * from snowy leaves when it thaws or in a storm, with a soft thump. The sound comes from the tree itself.
     */
    public static void snowClumps(Level level, BlockPos pos, RandomSource random) {
        if (!ClientConfig.snowFallingFromTrees()) {
            return;
        }
        Biome biome = level.getBiome(pos).value();
        if (SeasonalColorState.snowOnLeaves(biome) < 0.3 || !level.getBlockState(pos.below()).isAir()) {
            return;
        }
        double dailyMean = WorldClimate.dailyMeanNow(biome, pos);
        boolean thawing = !Double.isNaN(dailyMean) && dailyMean > -1.0;
        double chance = 0.002 * ((thawing ? 3 : 0) + (level.isRaining() ? 2 : 0));
        if (random.nextDouble() >= chance) {
            return;
        }
        BlockParticleOption snow = new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SNOW_BLOCK.defaultBlockState());
        for (int i = 0; i < 8; i++) {
            level.addParticle(snow, pos.getX() + random.nextDouble(), pos.getY() - 0.05, pos.getZ() + random.nextDouble(), 0, 0, 0);
        }
        level.playLocalSound(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, SoundEvents.SNOW_FALL, SoundSource.BLOCKS,
                0.4F, 0.9F, false);
    }

    /** How windy it is in the trees: calm in summer, breezier in autumn, much more in rain and storms. */
    public static float windFactor(Level level, BlockPos pos) {
        double autumn = SeasonalColorState.leafAutumn(level.getBiome(pos).value());
        double storm = 1.0 + 1.5 * level.getRainLevel(1.0F) + 2.0 * level.getThunderLevel(1.0F);
        return (float) ((1.0 + 1.5 * autumn) * storm);
    }
}
