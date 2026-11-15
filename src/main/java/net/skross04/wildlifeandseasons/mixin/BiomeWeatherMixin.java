package net.skross04.wildlifeandseasons.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.skross04.wildlifeandseasons.climate.WorldClimate;
import net.skross04.wildlifeandseasons.ecology.climate.SnowAndIce;

/**
 * Vanilla decides "rain or snow?" in {@code warmEnoughToRain}, and uses that same answer for snow piling up,
 * water freezing and which weather the client draws. We answer it with the seasonal climate temperature,
 * so all of those follow the seasons at once.
 * <p>
 * {@code @At("HEAD")}: runs before vanilla's own code; setting a return value skips the original.
 */
@Mixin(Biome.class)
public abstract class BiomeWeatherMixin {
    @Inject(method = "warmEnoughToRain", at = @At("HEAD"), cancellable = true)
    private void wildlifeandseasons$seasonalRainOrSnow(BlockPos pos, int seaLevel, CallbackInfoReturnable<Boolean> cir) {
        double temperature = WorldClimate.temperatureNow((Biome) (Object) this, pos);
        if (!Double.isNaN(temperature)) {
            cir.setReturnValue(SnowAndIce.isRainTemperature(temperature));
        }
    }

    /** Wet and dry seasons: savannas and jungles only rain in their wet season, deserts on a few spring days. */
    @Inject(method = "hasPrecipitation", at = @At("HEAD"), cancellable = true)
    private void wildlifeandseasons$wetAndDrySeasons(CallbackInfoReturnable<Boolean> cir) {
        Boolean rain = WorldClimate.wetSeasonRain((Biome) (Object) this);
        if (rain != null) {
            cir.setReturnValue(rain);
        }
    }
}
