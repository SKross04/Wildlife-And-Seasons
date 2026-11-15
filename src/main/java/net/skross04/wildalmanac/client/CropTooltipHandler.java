package net.skross04.wildalmanac.client;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.ecology.season.Season;
import net.skross04.wildalmanac.farming.CropSeasons;
import net.skross04.wildalmanac.season.SeasonAccess;
import net.skross04.wildalmanac.season.SeasonTranslations;

/**
 * Adds "Grows in:" plus one coloured line per season to seeds and other items you plant.
 * {@code value = Dist.CLIENT}: only loaded on the player's computer, never on a dedicated server.
 */
@EventBusSubscriber(modid = WildAlmanac.MODID, value = Dist.CLIENT)
public class CropTooltipHandler {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!(event.getItemStack().getItem() instanceof BlockItem blockItem)) {
            return;
        }
        EnumSet<Season> seasons = CropSeasons.seasonsFor(blockItem.getBlock().defaultBlockState());
        if (seasons == null) {
            return; // not a seasonal crop
        }
        // "Grows in:" with each season on its own line, in that season's colour.
        List<Component> tooltip = event.getToolTip();
        tooltip.add(Component.translatable(SeasonTranslations.CROP_GROWS_IN).withStyle(ChatFormatting.GRAY));
        for (Season season : seasons) {
            tooltip.add(Component.literal("  ")
                    .append(Component.translatable(SeasonTranslations.seasonNameKey(season)))
                    .withStyle(colorOf(season)));
        }
    }

    /** Forget the season when leaving a world or server. */
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        SeasonAccess.clearClient();
    }

    private static ChatFormatting colorOf(Season season) {
        return switch (season) {
            case SPRING -> ChatFormatting.GREEN;
            case SUMMER -> ChatFormatting.YELLOW;
            case AUTUMN -> ChatFormatting.GOLD;
            case WINTER -> ChatFormatting.AQUA;
        };
    }
}
