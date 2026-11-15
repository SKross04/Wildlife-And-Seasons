package net.skross04.wildlifeandseasons.farming;

import java.util.EnumSet;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.skross04.wildlifeandseasons.Config;
import net.skross04.wildlifeandseasons.climate.WorldClimate;
import net.skross04.wildlifeandseasons.ecology.farming.CropSeasonRules;
import net.skross04.wildlifeandseasons.ecology.season.Season;
import net.skross04.wildlifeandseasons.season.SeasonAccess;
import net.skross04.wildlifeandseasons.util.ModTags;

/**
 * Connects the crop season rules to Minecraft blocks: which seasons a crop grows in (from block tags)
 * and whether it stands in a greenhouse.
 */
public final class CropSeasons {
    /** Crops from other mods that are in vanilla's crop tag but in none of our season tags. */
    private static final EnumSet<Season> DEFAULT_SEASONS = EnumSet.of(Season.SPRING, Season.SUMMER, Season.AUTUMN);

    private CropSeasons() {
    }

    /** The seasons this block grows in, or null if seasons don't affect it. */
    public static @Nullable EnumSet<Season> seasonsFor(BlockState state) {
        if (state.is(ModTags.Blocks.GROWS_ALL_YEAR)) {
            return null;
        }
        EnumSet<Season> seasons = EnumSet.noneOf(Season.class);
        for (Season season : Season.values()) {
            if (state.is(ModTags.Blocks.growsIn(season))) {
                seasons.add(season);
            }
        }
        if (!seasons.isEmpty()) {
            return seasons.size() == Season.values().length ? null : seasons;
        }
        return state.is(ModTags.Blocks.VANILLA_CROPS) ? DEFAULT_SEASONS : null;
    }

    /** True if the crop may grow right now (also true for blocks seasons don't affect). */
    public static boolean isInSeason(Level level, BlockPos pos, BlockState state) {
        EnumSet<Season> seasons = seasonsFor(state);
        if (seasons == null) {
            return true;
        }
        Optional<Season> current = SeasonAccess.currentSeason(level);
        if (current.isEmpty()) {
            return true; // client hasn't heard the season yet: don't block anything
        }
        return CropSeasonRules.isInSeason(seasons, current.get(), WorldClimate.zoneOf(level.getBiome(pos)),
                () -> isGreenhouse(level, pos));
    }

    /**
     * A crop is in a greenhouse when the first solid block above it is glass.
     * Things you can walk through (other crops, torches, flowers) are looked past; walls aren't needed.
     */
    public static boolean isGreenhouse(Level level, BlockPos pos) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        int maxHeight = Config.greenhouseMaxHeight();
        for (int i = 0; i < maxHeight; i++) {
            cursor.move(Direction.UP);
            if (level.isOutsideBuildHeight(cursor)) {
                return false;
            }
            BlockState above = level.getBlockState(cursor);
            if (above.is(ModTags.Blocks.GREENHOUSE_GLASS)) {
                return true;
            }
            if (!above.getCollisionShape(level, cursor).isEmpty()) {
                return false; // a solid roof that isn't glass
            }
        }
        return false;
    }
}
