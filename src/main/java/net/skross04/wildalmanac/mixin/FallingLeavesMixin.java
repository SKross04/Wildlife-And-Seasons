package net.skross04.wildalmanac.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingParticlesLeavesBlock;
import net.skross04.wildalmanac.ClientConfig;
import net.skross04.wildalmanac.client.SeasonalColorState;

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
    private float wildalmanac$seasonalLeafFall(float chance, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        if (!ClientConfig.seasonalFallingLeaves()) {
            return chance;
        }
        return chance * SeasonalColorState.fallingLeavesFactor(level.getBiome(pos).value(), level.getBlockState(pos));
    }
}
