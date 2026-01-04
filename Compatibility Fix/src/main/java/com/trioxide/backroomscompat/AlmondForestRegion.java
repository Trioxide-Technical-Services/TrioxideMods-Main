package com.trioxide.backroomscompat;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
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
        // Add all vanilla biomes first
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            // Don't modify vanilla biomes - keep them all
        });

        // Then add Almond Forest at specific narrow parameters
        // These parameters are chosen to:
        // 1. Be narrow (making it rare)
        // 2. Overlap with forest-like terrain (ensuring correct surface generation)
        // 3. Use continentalness values that produce surface terrain
        Climate.ParameterPoint almondForestPoint = new Climate.ParameterPoint(
            Climate.Parameter.span(0.15f, 0.35f),   // Temperature: narrow temperate range
            Climate.Parameter.span(0.1f, 0.3f),     // Humidity: slightly humid
            Climate.Parameter.span(0.5f, 0.7f),     // Continentalness: mid-inland (surface terrain)
            Climate.Parameter.span(-0.1f, 0.1f),    // Erosion: low erosion (flatter terrain)
            Climate.Parameter.point(0.0f),          // Depth: surface only
            Climate.Parameter.span(-0.05f, 0.05f),  // Weirdness: very normal
            0L                                       // Offset
        );

        mapper.accept(Pair.of(almondForestPoint, ALMOND_FOREST));
    }
}
