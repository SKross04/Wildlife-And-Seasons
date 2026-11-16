package net.skross04.wildlifeandseasons.client;

import java.util.OptionalDouble;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.skross04.wildlifeandseasons.ClientConfig;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.climate.BiomeZones;
import net.skross04.wildlifeandseasons.climate.ClimateSnapshot;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.climate.SkyRules;
import net.skross04.wildlifeandseasons.particle.ModParticles;
import net.skross04.wildlifeandseasons.season.SeasonAccess;

/**
 * Puts the northern lights and shooting stars in the night sky above the player (see SkyRules). Purely visual, only
 * on the player's screen, only under a clear night sky in the overworld. Every player sees the same nights (date +
 * the world's weather seed); the exact streaks differ a little per player.
 */
@EventBusSubscriber(modid = WildlifeAndSeasons.MODID, value = Dist.CLIENT)
public final class SkyShow {
    /** How bright the aurora is right now (read by new aurora particles). */
    private static double aurora;

    private SkyShow() {
    }

    static double auroraBrightness() {
        return aurora;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        Player player = minecraft.player;
        if (level == null || player == null || minecraft.isPaused() || level.dimension() != Level.OVERWORLD) {
            aurora = 0;
            return;
        }
        OptionalDouble year = SeasonAccess.yearPosition(level);
        long day = SeasonAccess.dayIndex(level);
        BlockPos pos = player.blockPosition();
        if (year.isEmpty() || day < 0 || level.getRainLevel(1.0F) > 0.1F || !level.canSeeSky(pos.above())) {
            aurora = 0;
            return;
        }
        double dayPosition = ClimateSnapshot.dayPosition(level);
        // the night belongs to the day it started on (after midnight the calendar day has already moved on)
        long night = dayPosition < 0.5 ? day - 1 : day;
        ClimateZone zone = BiomeZones.of(level.getBiome(pos).value()).zone();
        double distance = Math.min(110, minecraft.options.getEffectiveRenderDistance() * 16 * 0.6);
        RandomSource random = level.getRandom();

        aurora = ClientConfig.northernLights()
                ? SkyRules.auroraNow(SkyRules.auroraNight(zone, year.getAsDouble(), night, SeasonAccess.weatherSeed(level)),
                        dayPosition, level.getGameTime())
                : 0;
        if (aurora > 0.02) {
            spawnAurora(minecraft, player, distance, random);
        }
        if (ClientConfig.shootingStars() && random.nextDouble() < SkyRules.meteorsPerTick(year.getAsDouble(), dayPosition)) {
            spawnShootingStar(minecraft, player, distance, random);
        }
    }

    /**
     * Aurora curtains to the north (−Z), a long wavy band high up: green in the middle, a faint red glow above,
     * and in strong displays a violet lower edge.
     */
    private static void spawnAurora(Minecraft minecraft, Player player, double distance, RandomSource random) {
        double count = aurora * 3;
        int n = (int) count + (random.nextDouble() < count - (int) count ? 1 : 0);
        long time = player.level().getGameTime();
        for (int i = 0; i < n; i++) {
            double s = random.nextDouble() * 2 - 1;
            double x = player.getX() + s * distance * 1.3;
            double z = player.getZ() - distance + distance * 0.22 * Math.sin(s * 3.1 + time * 0.0015)
                    + distance * 0.08 * Math.sin(s * 9.0 - time * 0.004);
            double y = player.getEyeY() + distance * 0.5 + random.nextDouble() * distance * 0.06;
            create(minecraft, x, y, z, 0.30, 1.00, 0.55);
            if (random.nextDouble() < 0.3 * aurora) {
                create(minecraft, x, y + 11, z - 4, 0.95, 0.25, 0.35);    // red oxygen glow higher up
            }
            if (aurora > 0.6 && random.nextDouble() < 0.2) {
                create(minecraft, x, y - 7, z, 0.62, 0.35, 1.00);         // violet nitrogen lower edge
            }
        }
    }

    private static void create(Minecraft minecraft, double x, double y, double z, double r, double g, double b) {
        minecraft.particleEngine.createParticle(ModParticles.AURORA.get(), x, y, z, r, g, b);
    }

    /** A shooting star somewhere in the sky, flying roughly sideways and a little down. */
    private static void spawnShootingStar(Minecraft minecraft, Player player, double distance, RandomSource random) {
        double angle = random.nextDouble() * Math.PI * 2;
        double x = player.getX() + Math.cos(angle) * distance * (0.5 + random.nextDouble() * 0.5);
        double z = player.getZ() + Math.sin(angle) * distance * (0.5 + random.nextDouble() * 0.5);
        double y = player.getEyeY() + distance * (0.55 + random.nextDouble() * 0.25);
        double direction = random.nextDouble() * Math.PI * 2;
        double speed = 2.5 + random.nextDouble() * 1.5;
        minecraft.particleEngine.createParticle(ModParticles.SHOOTING_STAR.get(), x, y, z,
                Math.cos(direction) * speed, -0.4 - random.nextDouble() * 0.6, Math.sin(direction) * speed);
    }
}
