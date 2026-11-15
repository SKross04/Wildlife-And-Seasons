package net.skross04.wildalmanac.farming;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.skross04.wildalmanac.Config;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.ecology.farming.CropSeasonRules;

@EventBusSubscriber(modid = WildAlmanac.MODID)
public class FarmingEvents {
    /**
     * NeoForge asks this every time a crop wants to grow a stage (vanilla crops and most modded ones).
     * Out of season, outside a greenhouse, the answer is "no".
     */
    @SubscribeEvent
    public static void onCropGrow(CropGrowEvent.Pre event) {
        if (!(event.getLevel() instanceof ServerLevel level) || event.getResult() == CropGrowEvent.Pre.Result.DO_NOT_GROW) {
            return;
        }
        if (CropSeasons.isInSeason(level, event.getPos(), event.getState())) {
            return;
        }
        if (CropSeasonRules.growsAnyway(Config.outOfSeasonGrowth(), level.getRandom().nextDouble())) {
            return;
        }
        event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
    }

    /** Bone meal doesn't work on out-of-season crops either (otherwise it would be an easy way around the seasons). */
    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        if (Config.boneMealOutOfSeason() || !event.isValidBonemealTarget()) {
            return;
        }
        if (!CropSeasons.isInSeason(event.getLevel(), event.getPos(), event.getState())) {
            event.setSuccessful(false); // cancels it: no growth, bone meal not used up
        }
    }
}
