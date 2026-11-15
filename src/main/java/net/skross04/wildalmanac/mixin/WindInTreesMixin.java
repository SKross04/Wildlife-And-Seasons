package net.skross04.wildalmanac.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.sounds.AmbientLeavesBlockSoundPlayer;
import net.skross04.wildalmanac.ClientConfig;
import net.skross04.wildalmanac.client.Atmosphere;

/**
 * Vanilla leaves sometimes rustle (a 1 in {@code chance} chance per animation tick). On windy days the leaves
 * rustle more often: more in autumn, much more in rain and storms. The sound still comes from the leaves.
 */
@Mixin(AmbientLeavesBlockSoundPlayer.class)
public abstract class WindInTreesMixin {
    @ModifyExpressionValue(method = "playAmbientLeavesSounds",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/sounds/AmbientLeavesBlockSoundPlayer;chance:I"))
    private int wildalmanac$windyRustling(int chance, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        if (!ClientConfig.windInTrees()) {
            return chance;
        }
        return Math.max(1, Math.round(chance / Atmosphere.windFactor(level, pos)));
    }
}
