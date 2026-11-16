package net.skross04.wildlifeandseasons.compat.jade;

import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * Jade (optional): looking at a crop shows whether it's in season. Jade finds this class by its
 * {@link WailaPlugin} annotation; without Jade it's never loaded.
 */
@WailaPlugin
public class WildlifeAndSeasonsJadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // For every block: the provider itself skips anything that isn't a seasonal crop (a quick tag check).
        registration.registerBlockComponent(CropSeasonProvider.INSTANCE, Block.class);
    }
}
