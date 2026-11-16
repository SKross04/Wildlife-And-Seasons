package net.skross04.wildlifeandseasons.util;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;
import net.skross04.wildlifeandseasons.ecology.season.Season;

public class ModTags {
    public static class Blocks {
        /** Crops that grow in a season: {@code wildlifeandseasons:grows_in/spring}, {@code .../summer}, … */
        private static final Map<Season, TagKey<Block>> GROWS_IN = new EnumMap<>(Season.class);

        static {
            for (Season season : Season.values()) {
                GROWS_IN.put(season, createTag("grows_in/" + season.id()));
            }
        }

        /** Crops that ignore seasons (empty by default; for datapacks). */
        public static final TagKey<Block> GROWS_ALL_YEAR = createTag("grows_all_year");

        /** Roof blocks that make a greenhouse: all glass blocks and panes by default. */
        public static final TagKey<Block> GREENHOUSE_GLASS = createTag("greenhouse_glass");

        /** Leaves that turn colour and drop in autumn (falling leaves, leaf litter). */
        public static final TagKey<Block> DROPS_LEAF_LITTER = createTag("drops_leaf_litter");

        /** Flowers that bloom in spring and wilt (wild) or go dormant (planted) the rest of the year. */
        public static final TagKey<Block> SEASONAL_FLOWERS = createTag("seasonal_flowers");
        /**
         * Low plants that snow covers in winter instead of leaving holes in it (short and tall grass, ferns, bushes).
         * They grow back in the same spot when the snow melts.
         */
        public static final TagKey<Block> BURIED_BY_SNOW = createTag("buried_by_snow");
        /** Tall (two-block) flowers that bloom in spring and summer and wilt or go dormant the rest of the year. */
        public static final TagKey<Block> SEASONAL_TALL_FLOWERS = createTag("seasonal_tall_flowers");
        /** Exceptions: flowers in the seasonal tag that should stay as they are (e.g. wither rose). */
        public static final TagKey<Block> NOT_SEASONAL_FLOWERS = createTag("not_seasonal_flowers");

        /** The desert bloom's flowers (they follow desert rain, not the normal flower seasons). */
        public static final TagKey<Block> DESERT_FLOWERS = createTag("desert_flowers");

        /** What desert flowers grow on: sand, red sand, dirt and terracotta. */
        public static final TagKey<Block> DESERT_FLOWER_SOIL = createTag("desert_flower_soil");

        /** Flowers that bloom in late winter, before spring (snowdrops), and wilt by early spring. */
        public static final TagKey<Block> BLOOMS_LATE_WINTER = createTag("blooms_late_winter");

        /** Flowers that bloom at the very start of spring (crocuses), before the other flowers. */
        public static final TagKey<Block> BLOOMS_EARLY_SPRING = createTag("blooms_early_spring");

        /** Vanilla's crop tag. Crops from other mods that are in it, but in no season tag, get spring–autumn. */
        public static final TagKey<Block> VANILLA_CROPS =
                TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("crops"));

        public static TagKey<Block> growsIn(Season season) {
            return GROWS_IN.get(season);
        }

        private static TagKey<Block> createTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(WildlifeAndSeasons.MODID, name));
        }
    }

    public static class Biomes {
        /**
         * One biome tag per climate zone: {@code wildlifeandseasons:climate_zone/temperate}, {@code .../cold}, …
         * Modpack makers can change which biomes are in which zone with a datapack.
         */
        private static final Map<ClimateZone, TagKey<Biome>> CLIMATE_ZONES = new EnumMap<>(ClimateZone.class);

        static {
            for (ClimateZone zone : ClimateZone.values()) {
                CLIMATE_ZONES.put(zone, createTag("climate_zone/" + zone.id()));
            }
        }

        /**
         * Biomes whose own grass and leaf colours are autumn colours (the Dappled Forest). With seasonal colours their
         * grass and leaves are green in spring and summer and only take the biome's own colours in autumn.
         */
        public static final TagKey<Biome> AUTUMN_COLORED = createTag("autumn_colored");

        /** Biomes where mushrooms come up on the forest floor in autumn (forests and taigas by default). */
        public static final TagKey<Biome> HAS_AUTUMN_MUSHROOMS = createTag("has_autumn_mushrooms");

        /** Biomes where the desert bloom happens after rain (deserts and badlands by default; arid climate needed). */
        public static final TagKey<Biome> HAS_DESERT_BLOOM = createTag("has_desert_bloom");

        /** Biomes where snowdrops grow (woods by default). */
        public static final TagKey<Biome> HAS_SNOWDROPS = createTag("has_snowdrops");

        /** Biomes where crocuses grow (meadows and plains by default). */
        public static final TagKey<Biome> HAS_CROCUSES = createTag("has_crocuses");

        /** Biomes where leaf litter gathers in autumn (forests by default). */
        public static final TagKey<Biome> HAS_LEAF_LITTER = createTag("has_leaf_litter");

        public static TagKey<Biome> climateZone(ClimateZone zone) {
            return CLIMATE_ZONES.get(zone);
        }

        private static TagKey<Biome> createTag(String name) {
            return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(WildlifeAndSeasons.MODID, name));
        }
    }
}
