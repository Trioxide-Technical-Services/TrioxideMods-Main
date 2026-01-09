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

    // Our own Almond Forest biome
    public static final ResourceKey<Biome> ALMOND_FOREST = ResourceKey.create(
        net.minecraft.core.registries.Registries.BIOME,
        new ResourceLocation("backrooms_terralith_compat", "almond_forest")
    );

    public AlmondForestRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        // Add Almond Forest with explicit climate parameters
        // Using depth = 0 (point value) ensures surface-only spawning
        // This works better with Tectonic's modified terrain generation

        // Temperature: mild (like meadow/forest)
        Climate.Parameter temperature = Climate.Parameter.span(0.0f, 0.5f);
        // Humidity: moderate
        Climate.Parameter humidity = Climate.Parameter.span(-0.35f, 0.1f);
        // Continentalness: inland areas (not ocean/coast)
        Climate.Parameter continentalness = Climate.Parameter.span(0.03f, 0.8f);
        // Erosion: moderate (not too flat, not too steep)
        Climate.Parameter erosion = Climate.Parameter.span(-0.5f, 0.5f);
        // Depth: 0 = surface only (critical for not spawning underground!)
        Climate.Parameter depth = Climate.Parameter.point(0.0f);
        // Weirdness: normal terrain
        Climate.Parameter weirdness = Climate.Parameter.span(-0.4f, 0.4f);

        Climate.ParameterPoint almondForestParams = new Climate.ParameterPoint(
            temperature,
            humidity,
            continentalness,
            erosion,
            depth,
            weirdness,
            0L // offset
        );

        // Add the biome with our explicit parameters
        mapper.accept(Pair.of(almondForestParams, ALMOND_FOREST));
    }
}
