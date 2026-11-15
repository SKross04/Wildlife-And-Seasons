package net.skross04.wildalmanac.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.level.biome.Biome;

/**
 * An "accessor" mixin: it doesn't change anything, it just lets us call a private Biome method.
 * We need the biome's original leaf colour (before our seasonal change) to mix in red/orange/yellow patches.
 */
@Mixin(Biome.class)
public interface BiomeAccessor {
    @Invoker("getFoliageColorFromTexture")
    int wildalmanac$getFoliageColorFromTexture();
}
