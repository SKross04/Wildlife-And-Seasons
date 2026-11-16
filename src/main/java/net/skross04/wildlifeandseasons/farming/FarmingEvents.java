package net.skross04.wildlifeandseasons.farming;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.skross04.wildlifeandseasons.Config;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.ecology.farming.CropSeasonRules;

@EventBusSubscriber(modid = WildlifeAndSeasons.MODID)
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
        BlockState state = event.getState();
        if (state.getBlock() instanceof SweetBerryBushBlock
                && !BerryBushes.mayGrowTo(level, event.getPos(), state, state.getValue(SweetBerryBushBlock.AGE) + 1)) {
            event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW); // berries only after flowering, ripe in late summer
            return;
        }
        if (CropSeasons.isInSeason(level, event.getPos(), state)) {
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
        BlockState state = event.getState();
        if (!CropSeasons.isInSeason(event.getLevel(), event.getPos(), state)
                || state.getBlock() instanceof SweetBerryBushBlock
                && !BerryBushes.mayGrowTo(event.getLevel(), event.getPos(), state, state.getValue(SweetBerryBushBlock.AGE) + 1)) {
            event.setSuccessful(false); // cancels it: no growth, bone meal not used up
        }
    }
}
