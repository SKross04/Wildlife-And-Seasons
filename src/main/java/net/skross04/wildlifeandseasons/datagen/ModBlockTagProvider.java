package net.skross04.wildlifeandseasons.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.ecology.season.Season;
import net.skross04.wildlifeandseasons.util.ModTags;

/**
 * Which crops grow in which season, and what counts as greenhouse glass.
 * Everything here can be changed by modpack makers with a datapack.
 */
public class ModBlockTagProvider extends BlockTagsProvider {
    private static final String FARMERS_DELIGHT = "farmersdelight";

    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, WildlifeAndSeasons.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        // --- Vanilla crops ---
        growsIn(Season.SPRING, Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS,
                Blocks.SUGAR_CANE, Blocks.TORCHFLOWER_CROP, Blocks.SWEET_BERRY_BUSH);
        growsIn(Season.SUMMER, Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM,
                Blocks.SUGAR_CANE, Blocks.TORCHFLOWER_CROP, Blocks.SWEET_BERRY_BUSH, Blocks.COCOA);
        growsIn(Season.AUTUMN, Blocks.WHEAT, Blocks.CARROTS, Blocks.BEETROOTS, Blocks.PUMPKIN_STEM, Blocks.SWEET_BERRY_BUSH);
        // Beetroot is the hardy winter crop, so winter isn't a dead season.
        growsIn(Season.WINTER, Blocks.BEETROOTS);

        // --- Farmer's Delight (optional entries: ignored when the mod isn't installed) ---
        growsInOptional(Season.SPRING, FARMERS_DELIGHT, "cabbages", "onions", "rice", "rice_panicles");
        growsInOptional(Season.SUMMER, FARMERS_DELIGHT, "onions", "rice", "rice_panicles",
                "budding_tomatoes", "tomatoes", "tomatoes_on_rope");
        growsInOptional(Season.AUTUMN, FARMERS_DELIGHT, "cabbages", "onions");

        // Empty on purpose: for datapacks that want a crop to ignore seasons.
        tag(ModTags.Blocks.GROWS_ALL_YEAR);

        // Leaves that drop in autumn. Evergreens (spruce) and tropical leaves aren't in it.
        var deciduous = tag(ModTags.Blocks.DROPS_LEAF_LITTER);
        for (Block leaves : new Block[]{Blocks.OAK_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.BIRCH_LEAVES,
                Blocks.RED_POPLAR_LEAVES, Blocks.ORANGE_POPLAR_LEAVES, Blocks.YELLOW_POPLAR_LEAVES}) {
            deciduous.add(TagEntry.element(BuiltInRegistries.BLOCK.getKey(leaves)));
        }

        // Seasonal flowers: all small flowers (also modded ones in that tag), pink petals and wildflowers.
        tag(ModTags.Blocks.SEASONAL_FLOWERS)
                .addTag(BlockTags.SMALL_FLOWERS)
                .add(TagEntry.element(BuiltInRegistries.BLOCK.getKey(Blocks.PINK_PETALS)))
                .add(TagEntry.element(BuiltInRegistries.BLOCK.getKey(Blocks.WILDFLOWERS)));
        // Plants that snow covers in winter (and that come back when it melts). Bush is optional: added in 1.21.5.
        var buriedBySnow = tag(ModTags.Blocks.BURIED_BY_SNOW);
        // The Dappled Forest's red shrub stays red all year, but snow covers it like the other low plants.
        // Sweet berry bushes are low shrubs too: snowed under in winter, back with their berries in spring
        // (real lingonberries spend the winter under the snow).
        for (Block block : new Block[]{Blocks.SHORT_GRASS, Blocks.TALL_GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.RED_SHRUB,
                Blocks.SWEET_BERRY_BUSH}) {
            buriedBySnow.add(TagEntry.element(BuiltInRegistries.BLOCK.getKey(block)));
        }
        buriedBySnow.add(TagEntry.optionalElement(Identifier.fromNamespaceAndPath("minecraft", "bush")));

        // Seasonal tall flowers: the four vanilla ones, plus modded tall flowers in the common c:flowers/tall tag.
        var tallFlowers = tag(ModTags.Blocks.SEASONAL_TALL_FLOWERS);
        for (Block block : new Block[]{Blocks.LILAC, Blocks.PEONY, Blocks.ROSE_BUSH, Blocks.SUNFLOWER}) {
            tallFlowers.add(TagEntry.element(BuiltInRegistries.BLOCK.getKey(block)));
        }
        tallFlowers.add(TagEntry.optionalTag(Identifier.fromNamespaceAndPath("c", "flowers/tall")));
        // Not seasonal: the wither rose (not a wild flower), eyeblossoms (they open and close every night already)
        // and the torchflower and pitcher plant (ancient plants grown from sniffer seeds).
        var notSeasonal = tag(ModTags.Blocks.NOT_SEASONAL_FLOWERS);
        for (Block block : new Block[]{Blocks.WITHER_ROSE, Blocks.OPEN_EYEBLOSSOM, Blocks.CLOSED_EYEBLOSSOM, Blocks.TORCHFLOWER,
                Blocks.PITCHER_PLANT}) {
            notSeasonal.add(TagEntry.element(BuiltInRegistries.BLOCK.getKey(block)));
        }

        tag(ModTags.Blocks.GREENHOUSE_GLASS)
                .addTag(Tags.BlockItems.GLASS_BLOCKS.block())
                .addTag(Tags.BlockItems.GLASS_PANES.block());
    }

    private void growsIn(Season season, Block... blocks) {
        var appender = tag(ModTags.Blocks.growsIn(season));
        for (Block block : blocks) {
            appender.add(TagEntry.element(BuiltInRegistries.BLOCK.getKey(block)));
        }
    }

    private void growsInOptional(Season season, String modId, String... blockNames) {
        var appender = tag(ModTags.Blocks.growsIn(season));
        for (String name : blockNames) {
            appender.add(TagEntry.optionalElement(Identifier.fromNamespaceAndPath(modId, name)));
        }
    }
}
