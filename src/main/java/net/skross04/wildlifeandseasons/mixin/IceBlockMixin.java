package net.skross04.wildlifeandseasons.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.skross04.wildlifeandseasons.climate.SeasonalThaw;

/** Ice open to the sky melts back into water when spring gets warm. */
@Mixin(IceBlock.class)
public abstract class IceBlockMixin {
    /** {@code @Shadow}: lets us call IceBlock's own (protected) melt method. */
    @Shadow
    protected abstract void melt(BlockState state, Level level, BlockPos pos);

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void wildlifeandseasons$seasonalThaw(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (SeasonalThaw.shouldMelt(level, pos, random)) {
            melt(state, level, pos);
            ci.cancel();
        }
    }
}
