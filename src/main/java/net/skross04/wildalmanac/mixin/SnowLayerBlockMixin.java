package net.skross04.wildalmanac.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.skross04.wildalmanac.climate.SeasonalThaw;
import net.skross04.wildalmanac.nature.Icicles;

/** Snow open to the sky melts away one layer at a time when it gets warm; snow on roof edges grows icicles. */
@Mixin(SnowLayerBlock.class)
public abstract class SnowLayerBlockMixin {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void wildalmanac$seasonalThaw(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (SeasonalThaw.shouldMelt(level, pos, random)) {
            int layers = state.getValue(SnowLayerBlock.LAYERS);
            if (layers > 1) {
                level.setBlockAndUpdate(pos, state.setValue(SnowLayerBlock.LAYERS, layers - 1));
            } else {
                level.removeBlock(pos, false);
            }
            ci.cancel();
            return;
        }
        Icicles.onSnowRandomTick(level, pos, random);
    }
}
