package net.skross04.wildlifeandseasons.client;

import java.util.OptionalDouble;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.util.RandomSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.skross04.wildlifeandseasons.ClientConfig;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.climate.BiomeZones;
import net.skross04.wildlifeandseasons.climate.ClimateSnapshot;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.climate.DayWeatherRules;
import net.skross04.wildlifeandseasons.ecology.season.SeasonDate;
import net.skross04.wildlifeandseasons.season.SeasonAccess;

/**
 * Foggy mornings and windy days on the player's screen (see DayWeatherRules). Every player in a world sees the same
 * days: they come from the date and the world's weather seed that the server sends.
 * Fog: the view distance closes in to about 32 blocks and the fog turns grey-white; not in caves, under water or
 * in rain. Wind: used by the rustling leaves and falling leaves. Blizzards: snow with strong wind closes the view in
 * to about 20 blocks, turns it white and blows snowflakes past the player (also lying snow on windy days).
 */
@EventBusSubscriber(modid = WildlifeAndSeasons.MODID, value = Dist.CLIENT)
public final class DayWeather {
    /** How thick the fog around the player is now (smoothed, so it rolls in and out gently). */
    private static double fog;
    /** How strong the blizzard around the player is now (smoothed). */
    private static double blizzard;

    private DayWeather() {
    }

    /** The weather character of a day at this place; null if the season isn't known yet. */
    private static DayWeatherRules.Day day(Level level, ClimateZone zone, int daysAhead) {
        OptionalDouble year = SeasonAccess.yearPosition(level);
        long day = SeasonAccess.dayIndex(level);
        if (year.isEmpty() || day < 0) {
            return null;
        }
        double p = year.getAsDouble() + daysAhead / (double) SeasonDate.DEFAULT_YEAR_LENGTH;
        return DayWeatherRules.day(zone, p - Math.floor(p), day + daysAhead, SeasonAccess.weatherSeed(level));
    }

    /** Hoarfrost per climate zone now (0..1), for the grass and leaf colours; less when it rains (clouds). */
    public static double[] frostByZone(Level level) {
        double[] amounts = new double[ClimateZone.values().length];
        OptionalDouble year = SeasonAccess.yearPosition(level);
        if (year.isEmpty() || level.dimension() != Level.OVERWORLD) {
            return amounts;
        }
        long timeOfDay = Math.round(ClimateSnapshot.dayPosition(level) * 24000);
        double clear = 1 - level.getRainLevel(1.0F);
        for (ClimateZone zone : ClimateZone.values()) {
            DayWeatherRules.Day today = day(level, zone, 0);
            DayWeatherRules.Day tomorrow = day(level, zone, 1);
            if (today != null && tomorrow != null) {
                amounts[zone.ordinal()] = DayWeatherRules.hoarfrost(today, tomorrow, year.getAsDouble(), timeOfDay) * clear;
            }
        }
        return amounts;
    }

    /** How hard the wind blows here now, 0..1 (0 when windy days are switched off or outside the overworld). */
    public static double windAt(Level level, BlockPos pos) {
        if (!ClientConfig.windyDays() || level.dimension() != Level.OVERWORLD) {
            return 0;
        }
        DayWeatherRules.Day today = day(level, BiomeZones.of(level.getBiome(pos).value()).zone(), 0);
        return today == null ? 0 : DayWeatherRules.wind(today, level.getGameTime());
    }

    /** How thick the morning fog should be here now, 0..1. */
    private static double fogAt(Level level, BlockPos pos) {
        if (!ClientConfig.foggyMornings() || level.dimension() != Level.OVERWORLD) {
            return 0;
        }
        ClimateZone zone = BiomeZones.of(level.getBiome(pos).value()).zone();
        DayWeatherRules.Day today = day(level, zone, 0);
        DayWeatherRules.Day tomorrow = day(level, zone, 1);
        OptionalDouble year = SeasonAccess.yearPosition(level);
        if (today == null || tomorrow == null || year.isEmpty()) {
            return 0;
        }
        long timeOfDay = Math.round(ClimateSnapshot.dayPosition(level) * 24000);   // 0 = sunrise
        double strength = DayWeatherRules.fog(today, tomorrow, year.getAsDouble(), timeOfDay);
        double outdoors = level.getBrightness(LightLayer.SKY, pos) / 15.0;     // none deep in caves
        return strength * outdoors * (1 - level.getRainLevel(1.0F));           // rain or clouds clear radiation fog
    }

    /** How strong a blizzard is here now, 0..1 (only out in the open, in the overworld). */
    private static double blizzardAt(Level level, BlockPos pos) {
        if (!ClientConfig.blizzards() || level.dimension() != Level.OVERWORLD || level.getBrightness(LightLayer.SKY, pos) < 8) {
            return 0;
        }
        Biome biome = level.getBiome(pos).value();
        boolean snowsHere = biome.getPrecipitationAt(pos, level.getSeaLevel()) == Biome.Precipitation.SNOW;
        if (!snowsHere) {
            return 0;
        }
        double wind = ClientConfig.windyDays() ? windAt(level, pos) : 0;
        BlockState feet = level.getBlockState(pos.below());
        BlockState at = level.getBlockState(pos.below(2));
        boolean snowOnGround = feet.is(Blocks.SNOW) || feet.is(Blocks.SNOW_BLOCK) || at.is(Blocks.SNOW_BLOCK)
                || at.is(Blocks.POWDER_SNOW) || level.getBlockState(pos.below()).is(Blocks.POWDER_SNOW);
        return DayWeatherRules.blizzard(level.getRainLevel(1.0F), wind, level.getThunderLevel(1.0F), snowOnGround);
    }

    /** Snowflakes blown past the player, along today's wind direction. */
    private static void blowSnow(Level level, Player player, RandomSource random) {
        int count = (int) Math.round(blizzard * 14);
        if (count <= 0) {
            return;
        }
        double direction = DayWeatherRules.windDirection(SeasonAccess.dayIndex(level), SeasonAccess.weatherSeed(level));
        double speed = 0.35 + 0.35 * blizzard;
        double dx = Math.cos(direction) * speed, dz = Math.sin(direction) * speed;
        for (int i = 0; i < count; i++) {
            double x = player.getX() + (random.nextDouble() - 0.5) * 20 - dx * 10;
            double y = player.getY() + random.nextDouble() * 7 - 1.5;
            double z = player.getZ() + (random.nextDouble() - 0.5) * 20 - dz * 10;
            if (level.canSeeSky(BlockPos.containing(x, y, z))) {
                level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, dx, -0.04 - random.nextDouble() * 0.04, dz);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.level == null) {
            fog = 0;
            blizzard = 0;
            return;
        }
        if (minecraft.isPaused()) {
            return;
        }
        BlockPos eyes = BlockPos.containing(player.getEyePosition());
        double target = fogAt(minecraft.level, eyes);
        fog += (target - fog) * 0.02;   // about 5 seconds to roll in or out
        double storm = blizzardAt(minecraft.level, eyes);
        blizzard += (storm - blizzard) * 0.04;
        blowSnow(minecraft.level, player, minecraft.level.getRandom());
    }

    /** Thick fog (about 32 blocks) and blizzards (about 20): only ever closer than normal, only in open air. */
    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        float strength = currentFog();
        float storm = currentBlizzard();
        if (strength <= 0.01F && storm <= 0.01F) {
            return;
        }
        float far = event.getFarPlaneDistance();
        float near = event.getNearPlaneDistance();
        far += (Math.min(far, DayWeatherRules.FOG_DISTANCE) - far) * strength;
        far += (Math.min(far, DayWeatherRules.BLIZZARD_DISTANCE) - far) * storm;
        event.setFarPlaneDistance(far);
        event.setNearPlaneDistance(Math.min(near, near * (1 - Math.max(strength, storm))));
    }

    /** Fog is grey-white, a blizzard snow-white: wash the colour out towards its own brightness (dark at night). */
    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        float strength = currentFog() * 0.7F;
        float storm = currentBlizzard() * 0.85F;
        if (strength <= 0.01F && storm <= 0.01F) {
            return;
        }
        float r = event.getRed(), g = event.getGreen(), b = event.getBlue();
        float light = 0.3F * r + 0.59F * g + 0.11F * b;
        float grey = Math.min(1.0F, light * 1.05F + 0.04F);
        float white = Math.min(1.0F, light * 1.25F + 0.12F);
        r += (grey - r) * strength; g += (grey - g) * strength; b += (grey - b) * strength;
        r += (white - r) * storm; g += (white - g) * storm; b += (white * 1.02F - b) * storm;
        event.setRed(r);
        event.setGreen(g);
        event.setBlue(Math.min(1.0F, b));
    }

    private static float currentBlizzard() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || blizzard <= 0.01 || !minecraft.level.getFluidState(BlockPos.containing(player.getEyePosition())).isEmpty()
                || player.hasEffect(MobEffects.BLINDNESS) || player.hasEffect(MobEffects.DARKNESS)) {
            return 0;
        }
        return (float) blizzard;
    }

    /** The fog to draw: none under water or lava, in powder snow, or with blindness/darkness (they have their own). */
    private static float currentFog() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.level == null || fog <= 0.01) {
            return 0;
        }
        BlockPos eyes = BlockPos.containing(player.getEyePosition());
        if (!minecraft.level.getFluidState(eyes).isEmpty() || player.isInPowderSnow
                || player.hasEffect(MobEffects.BLINDNESS) || player.hasEffect(MobEffects.DARKNESS)) {
            return 0;
        }
        return (float) fog;
    }
}
