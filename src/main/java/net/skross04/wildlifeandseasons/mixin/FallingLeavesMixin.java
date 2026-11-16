package net.skross04.wildlifeandseasons.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingParticlesLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.skross04.wildlifeandseasons.ClientConfig;
import net.skross04.wildlifeandseasons.client.Atmosphere;
import net.skross04.wildlifeandseasons.client.DayWeather;
import net.skross04.wildlifeandseasons.client.SeasonalColorState;

/**
 * Vanilla leaves sometimes drop a falling-leaf particle (a fixed small chance per leaf block).
 * We scale that chance with the season: many falling leaves in autumn, few the rest of the year.
 * The particles already take the leaves' colour, so autumn leaves fall orange, red and gold.
 * <p>
 * {@code @ModifyExpressionValue} (from MixinExtras, included with NeoForge): wherever the method reads the
 * {@code leafParticleChance} field, our method gets the value and returns the one that's used instead.
 * {@code @Local(argsOnly = true)} hands us the method's own parameters (the level and position).
 */
@Mixin(FallingParticlesLeavesBlock.class)
public abstract class FallingLeavesMixin {
    @ModifyExpressionValue(method = "makeFallingLeavesParticles",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/FallingParticlesLeavesBlock;leafParticleChance:F"))
    private float wildlifeandseasons$seasonalLeafFall(float chance, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        if (!ClientConfig.seasonalFallingLeaves()) {
            return chance;
        }
        return chance * SeasonalColorState.fallingLeavesFactor(level.getBiome(pos).value(), level.getBlockState(pos))
                * (float) (1.0 + 2.0 * DayWeather.windAt(level, pos));
    }

    /** Snow clumps falling from snowy trees (when it thaws, or in a storm). */
    @Inject(method = "animateTick", at = @At("HEAD"))
    private void wildlifeandseasons$snowClumps(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        Atmosphere.snowClumps(level, pos, random);
    }
}
