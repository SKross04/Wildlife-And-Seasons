package net.skross04.wildlifeandseasons.client;

import java.util.OptionalDouble;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
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
 * in rain. Wind: used by the rustling leaves and falling leaves.
 */
@EventBusSubscriber(modid = WildlifeAndSeasons.MODID, value = Dist.CLIENT)
public final class DayWeather {
    /** How thick the fog around the player is now (smoothed, so it rolls in and out gently). */
    private static double fog;

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

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.level == null) {
            fog = 0;
            return;
        }
        if (minecraft.isPaused()) {
            return;
        }
        double target = fogAt(minecraft.level, BlockPos.containing(player.getEyePosition()));
        fog += (target - fog) * 0.02;   // about 5 seconds to roll in or out
    }

    /** Thick fog: bring the view in to about 32 blocks (only ever closer than normal, only in open air). */
    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        float strength = currentFog();
        if (strength <= 0.01F) {
            return;
        }
        float far = event.getFarPlaneDistance();
        float near = event.getNearPlaneDistance();
        float foggyFar = Math.min(far, DayWeatherRules.FOG_DISTANCE);
        event.setFarPlaneDistance(far + (foggyFar - far) * strength);
        event.setNearPlaneDistance(Math.min(near, near * (1 - strength)));
    }

    /** Fog is grey-white: wash the colour out towards its own brightness (still dark at night). */
    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        float strength = currentFog() * 0.7F;
        if (strength <= 0.01F) {
            return;
        }
        float r = event.getRed(), g = event.getGreen(), b = event.getBlue();
        float grey = Math.min(1.0F, (0.3F * r + 0.59F * g + 0.11F * b) * 1.05F + 0.04F);
        event.setRed(r + (grey - r) * strength);
        event.setGreen(g + (grey - g) * strength);
        event.setBlue(b + (grey - b) * strength);
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
