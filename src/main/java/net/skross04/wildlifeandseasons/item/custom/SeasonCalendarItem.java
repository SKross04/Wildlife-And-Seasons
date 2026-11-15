package net.skross04.wildlifeandseasons.item.custom;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.skross04.wildlifeandseasons.climate.WorldClimate;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.climate.SeasonalWeather;
import net.skross04.wildlifeandseasons.season.SeasonTranslations;
import net.skross04.wildlifeandseasons.season.WorldSeasons;

/**
 * Right-click to see the current season and day, shown above the hotbar (not in chat).
 */
public class SeasonCalendarItem extends Item {
    public SeasonCalendarItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        // use() runs on both the client and the server. Only the server knows the real day count,
        // so the work happens there; the client just plays the arm-swing animation.
        if (level instanceof ServerLevel serverLevel) {
            player.sendOverlayMessage(describeHere(serverLevel, player));
            level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * The date, and in savannas and jungles also whether it's the wet or the dry season there
     * (those zones have wet and dry seasons instead of a cold winter).
     */
    private static Component describeHere(ServerLevel level, Player player) {
        Component date = SeasonTranslations.describe(WorldSeasons.currentDate(level));
        ClimateZone zone = WorldClimate.zoneOf(level.getBiome(player.blockPosition()));
        if (zone != ClimateZone.TROPICAL && zone != ClimateZone.SAVANNA) {
            return date;
        }
        double rainfall = WorldClimate.climateAt(level, player.blockPosition()).rainfall();
        boolean wet = Boolean.TRUE.equals(SeasonalWeather.hasRain(zone, rainfall));
        return Component.translatable(wet ? SeasonTranslations.WET_SEASON : SeasonTranslations.DRY_SEASON, date);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> components, TooltipFlag flag) {
        components.accept(Component.translatable(SeasonTranslations.CALENDAR_TOOLTIP).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipDisplay, components, flag);
    }
}
