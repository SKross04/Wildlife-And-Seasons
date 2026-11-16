package net.skross04.wildlifeandseasons.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.biome.Biome;
import net.skross04.wildlifeandseasons.client.SeasonalColorState;

/**
 * A mixin changes a Minecraft class while the game loads. This one adjusts the grass and leaf colours
 * every biome gives out, so all blocks that use biome colours (also modded grass and leaves) follow the seasons.
 * <p>
 * {@code @Inject(at = @At("RETURN"))}: run our code right before the original method returns,
 * and replace the colour it would have returned.
 */
@Mixin(Biome.class)
public abstract class BiomeColorMixin {
    @Inject(method = "getGrassColor", at = @At("RETURN"), cancellable = true)
    private void wildlifeandseasons$seasonalGrassColor(double x, double z, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(SeasonalColorState.frosted(SeasonalColorState.grass((Biome) (Object) this, cir.getReturnValueI()),
                (Biome) (Object) this, 0.65, 1.0));
    }

    @Inject(method = "getFoliageColor", at = @At("RETURN"), cancellable = true)
    private void wildlifeandseasons$seasonalFoliageColor(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(SeasonalColorState.frosted(SeasonalColorState.foliage((Biome) (Object) this, cir.getReturnValueI()),
                (Biome) (Object) this, 0.45, 1.0));
    }
}
