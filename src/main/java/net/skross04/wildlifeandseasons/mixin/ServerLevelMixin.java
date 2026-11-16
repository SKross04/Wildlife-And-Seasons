package net.skross04.wildlifeandseasons.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.skross04.wildlifeandseasons.nature.ChunkCatchUp;
import net.skross04.wildlifeandseasons.nature.DesertBloom;
import net.skross04.wildlifeandseasons.nature.SnowCover;

/**
 * Snowfall ({@code tickPrecipitation}) and the per-chunk tick.
 * <p>
 * Minecraft calls {@code tickChunk} for every chunk close enough to a player to get random ticks
 * (snow melting, flowers, crops…). That's how we know which chunks really kept up with the season.
 */
@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    /** Before vanilla's snowfall: snow that falls on grass or ferns covers them instead of leaving a hole. */
    @Inject(method = "tickPrecipitation", at = @At("HEAD"))
    private void wildlifeandseasons$snowCoversGrass(BlockPos column, CallbackInfo ci) {
        SnowCover.onSnowfall((ServerLevel) (Object) this, column);
    }

    @Inject(method = "tickChunk", at = @At("HEAD"))
    private void wildlifeandseasons$keepUpWithTheSeason(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        if (randomTickSpeed > 0) {
            ChunkCatchUp.onChunkTicked((ServerLevel) (Object) this, chunk);
            DesertBloom.onChunkTick((ServerLevel) (Object) this, chunk, randomTickSpeed);
        }
    }
}
