package net.skross04.wildlifeandseasons.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Transformable;
import net.skross04.wildlifeandseasons.nature.MossAndLichen;

/**
 * NeoForge data maps. Right now: an axe scrapes the moss off mossy cobblestone and stone bricks (and their stairs,
 * slabs and walls), like it scrapes copper, so moss that grew on a wall can always be removed. Datapacks can change it.
 */
public class ModDataMapProvider extends DataMapProvider {
    public ModDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        var transformables = builder(NeoForgeDataMaps.TRANSFORMABLES);
        MossAndLichen.MOSSY.forEach((clean, mossy) -> transformables.add(
                BuiltInRegistries.BLOCK.getResourceKey(mossy).orElseThrow(), Transformable.scraping(mossy, clean), false));
    }
}
