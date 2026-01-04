package com.trioxide.backroomscompat;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

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
        // Use VanillaParameterOverlayBuilder to add our biome alongside vanilla biomes
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

        // Add Almond Forest with climate parameters similar to regular forests
        // Temperature: 0.2 to 0.5 (moderate, like forest)
        // Humidity: 0.0 to 0.35 (slightly dry)
        // Continentalness: 0.3 to 1.0 (inland)
        // Erosion: -0.375 to 0.45 (varied terrain)
        // Weirdness: -0.2 to 0.2 (normal)
        // Depth: 0 (surface)

        new ParameterPointListBuilder()
            .temperature(Climate.Parameter.span(0.2f, 0.5f))
            .humidity(Climate.Parameter.span(0.0f, 0.35f))
            .continentalness(Climate.Parameter.span(0.3f, 1.0f))
            .erosion(Climate.Parameter.span(-0.375f, 0.45f))
            .weirdness(Climate.Parameter.span(-0.2f, 0.2f))
            .depth(Climate.Parameter.point(0.0f))
            .build()
            .forEach(parameterPoint -> builder.add(parameterPoint, ALMOND_FOREST));

        // Apply the overlay to the mapper
        builder.build().forEach(mapper);
    }

    /**
     * Helper class to build parameter points for biome placement
     */
    private static class ParameterPointListBuilder {
        private Climate.Parameter temperature = Climate.Parameter.span(-1.0f, 1.0f);
        private Climate.Parameter humidity = Climate.Parameter.span(-1.0f, 1.0f);
        private Climate.Parameter continentalness = Climate.Parameter.span(-1.0f, 1.0f);
        private Climate.Parameter erosion = Climate.Parameter.span(-1.0f, 1.0f);
        private Climate.Parameter weirdness = Climate.Parameter.span(-1.0f, 1.0f);
        private Climate.Parameter depth = Climate.Parameter.point(0.0f);

        public ParameterPointListBuilder temperature(Climate.Parameter temperature) {
            this.temperature = temperature;
            return this;
        }

        public ParameterPointListBuilder humidity(Climate.Parameter humidity) {
            this.humidity = humidity;
            return this;
        }

        public ParameterPointListBuilder continentalness(Climate.Parameter continentalness) {
            this.continentalness = continentalness;
            return this;
        }

        public ParameterPointListBuilder erosion(Climate.Parameter erosion) {
            this.erosion = erosion;
            return this;
        }

        public ParameterPointListBuilder weirdness(Climate.Parameter weirdness) {
            this.weirdness = weirdness;
            return this;
        }

        public ParameterPointListBuilder depth(Climate.Parameter depth) {
            this.depth = depth;
            return this;
        }

        public java.util.List<Climate.ParameterPoint> build() {
            return java.util.List.of(
                new Climate.ParameterPoint(
                    temperature,
                    humidity,
                    continentalness,
                    erosion,
                    depth,
                    weirdness,
                    0L  // offset
                )
            );
        }
    }
}
