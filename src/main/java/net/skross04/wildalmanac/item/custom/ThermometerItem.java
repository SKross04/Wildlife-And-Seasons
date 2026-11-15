package net.skross04.wildalmanac.item.custom;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.skross04.wildalmanac.climate.Instruments;

/** A pocket thermometer: right-click to read the temperature where you stand, shown above the hotbar. */
public class ThermometerItem extends Item {
    public ThermometerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        // Only the server knows the season, so it measures; the client just swings the arm.
        if (level instanceof ServerLevel serverLevel) {
            player.sendOverlayMessage(Instruments.thermometerReading(serverLevel, player.blockPosition()));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> components, TooltipFlag flag) {
        components.accept(Component.translatable(Instruments.THERMOMETER_TOOLTIP).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipDisplay, components, flag);
    }
}
