package net.skross04.wildalmanac.util;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.skross04.wildalmanac.WildAlmanac;
import net.skross04.wildalmanac.ecology.climate.ClimateZone;

public class ModTags {
    public static class Biomes {
        /**
         * One biome tag per climate zone: {@code wildalmanac:climate_zone/temperate}, {@code .../cold}, …
         * Modpack makers can change which biomes are in which zone with a datapack.
         */
        private static final Map<ClimateZone, TagKey<Biome>> CLIMATE_ZONES = new EnumMap<>(ClimateZone.class);

        static {
            for (ClimateZone zone : ClimateZone.values()) {
                CLIMATE_ZONES.put(zone, createTag("climate_zone/" + zone.id()));
            }
        }

        public static TagKey<Biome> climateZone(ClimateZone zone) {
            return CLIMATE_ZONES.get(zone);
        }

        private static TagKey<Biome> createTag(String name) {
            return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(WildAlmanac.MODID, name));
        }
    }
}
