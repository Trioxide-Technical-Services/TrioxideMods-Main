package com.trioxide.backroomscompat;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils.*;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class AlmondForestRegion extends Region {

    // Reference to the Backrooms mod's Almond Forest biome
    public static final ResourceKey<Biome> ALMOND_FOREST = ResourceKey.create(
        net.minecraft.core.registries.Registries.BIOME,
        new ResourceLocation("faithfulbackrooms", "almond_forest")
    );

    public AlmondForestRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        // Use vanilla biome builder but replace FOREST with ALMOND_FOREST in specific spots
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            // Add Almond Forest using ParameterUtils for correct vanilla-compatible parameters
            // These use the exact same parameter points that vanilla uses for forests
            builder.replaceBiome(Biomes.BIRCH_FOREST, ALMOND_FOREST);
        });
    }
}
