package net.skross04.wildlifeandseasons.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SpreadingSnowyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.skross04.wildlifeandseasons.nature.AutumnMushrooms;
import net.skross04.wildlifeandseasons.nature.LeafLitter;
import net.skross04.wildlifeandseasons.nature.SeasonalFlowers;

/**
 * Grass blocks are randomly ticked all the time (that's how grass spreads). We use those ticks to let
 * flowers wilt and bloom with the seasons, leaf litter gather and rot on forest floors, and mushrooms come up in autumn.
 * Vanilla's own code still runs afterwards (no cancel).
 */
@Mixin(SpreadingSnowyBlock.class)
public abstract class SpreadingSnowyBlockMixin {
    @Inject(method = "randomTick", at = @At("HEAD"))
    private void wildlifeandseasons$seasonalGround(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        SeasonalFlowers.onGroundRandomTick(level, pos, random);
        LeafLitter.onGrassRandomTick(state, level, pos, random);
        AutumnMushrooms.onGrassRandomTick(state, level, pos, random);
    }
}
