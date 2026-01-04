package com.trioxide.backroomscompat;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.core.registries.Registries;

public class AlmondForestSurfaceRules {

    public static final ResourceKey<Biome> ALMOND_FOREST = ResourceKey.create(
        Registries.BIOME,
        new ResourceLocation("backrooms_terralith_compat", "almond_forest")
    );

    public static SurfaceRules.RuleSource makeRules() {
        // Surface rule: When in Almond Forest biome, use grass/dirt surface
        SurfaceRules.RuleSource grassSurface = SurfaceRules.sequence(
            // On the floor (top surface), place grass block
            SurfaceRules.ifTrue(
                SurfaceRules.ON_FLOOR,
                SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState())
            ),
            // Under the floor (shallow underground), place dirt
            SurfaceRules.ifTrue(
                SurfaceRules.UNDER_FLOOR,
                SurfaceRules.state(Blocks.DIRT.defaultBlockState())
            )
        );

        // Only apply these rules when in the Almond Forest biome
        return SurfaceRules.ifTrue(
            SurfaceRules.isBiome(ALMOND_FOREST),
            grassSurface
        );
    }
}
