package net.skross04.wildlifeandseasons.mixin;

import java.util.OptionalInt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.skross04.wildlifeandseasons.client.SeasonalColorState;
import net.skross04.wildlifeandseasons.ecology.climate.SeasonalColors;

/**
 * Poplar leaves always drop Mojang's red, orange or yellow leaf particles. With seasonal poplars the leaves are
 * green in spring and summer, so outside their autumn colour they drop a normal leaf particle in the leaves'
 * current colour instead (green in summer, brown before they fall, …).
 */
@Mixin(UntintedParticleLeavesBlock.class)
public abstract class PoplarLeafParticlesMixin {
    @Inject(method = "spawnFallingLeavesParticle", at = @At("HEAD"), cancellable = true)
    private void wildlifeandseasons$seasonalPoplarParticle(Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        SeasonalColors.Poplar kind = SeasonalColorState.poplarKind(((Block) (Object) this).defaultBlockState());
        if (kind == null) {
            return;
        }
        OptionalInt color = SeasonalColorState.poplarParticleColor(level.getBiome(pos).value(),
                level.getClientLeafTintColor(pos), pos.getX(), pos.getZ());
        if (color.isPresent()) {
            ParticleUtils.spawnParticleBelow(level, pos, random, ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, color.getAsInt()));
            ci.cancel();
        }
    }
}
