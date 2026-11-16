package net.skross04.wildlifeandseasons.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.skross04.wildlifeandseasons.farming.BerryBushes;

/** Last year's berries drop when a sweet berry bush flowers again (vanilla's random tick only grows berries). */
@Mixin(SweetBerryBushBlock.class)
public abstract class SweetBerryBushMixin {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void wildlifeandseasons$oldBerriesFall(BlockState state, ServerLevel level, BlockPos pos, RandomSource random,
                                                   CallbackInfo ci) {
        if (BerryBushes.dropOldBerries(state, level, pos, random)) {
            ci.cancel();
        }
    }
}
