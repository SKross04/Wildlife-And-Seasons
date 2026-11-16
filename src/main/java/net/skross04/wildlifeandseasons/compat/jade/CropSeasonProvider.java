package net.skross04.wildlifeandseasons.compat.jade;

import java.util.EnumSet;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.compat.CompatTranslations;
import net.skross04.wildlifeandseasons.ecology.season.Season;
import net.skross04.wildlifeandseasons.farming.CropSeasons;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/** "In season" (green), "Out of season · Grows in: Spring, Summer" (red) or "Greenhouse: grows all year". */
public enum CropSeasonProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final Identifier UID = Identifier.fromNamespaceAndPath(WildlifeAndSeasons.MODID, "crop_season");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        EnumSet<Season> seasons = CropSeasons.seasonsFor(state);
        if (seasons == null) {
            return; // not a seasonal crop
        }
        Level level = accessor.getLevel();
        BlockPos pos = accessor.getPosition();
        if (CropSeasons.isGreenhouse(level, pos)) {
            tooltip.add(Component.translatable(CompatTranslations.JADE_GREENHOUSE).withStyle(ChatFormatting.GREEN));
        } else if (CropSeasons.isInSeason(level, pos, state)) {
            tooltip.add(Component.translatable(CompatTranslations.JADE_IN_SEASON).withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.translatable(CompatTranslations.JADE_OUT_OF_SEASON).withStyle(ChatFormatting.RED));
            tooltip.add(CompatTranslations.growsIn(seasons).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
