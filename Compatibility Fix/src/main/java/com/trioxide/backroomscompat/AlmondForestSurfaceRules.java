package com.trioxide.backroomscompat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class AlmondForestSurfaceRules {

    private static final SurfaceRules.RuleSource GRASS_BLOCK = SurfaceRules.state(
        Blocks.GRASS_BLOCK.defaultBlockState()
    );
    private static final SurfaceRules.RuleSource DIRT = SurfaceRules.state(
        Blocks.DIRT.defaultBlockState()
    );

    public static SurfaceRules.RuleSource makeRules() {
        // Create condition for Almond Forest biome
        SurfaceRules.ConditionSource isAlmondForest = SurfaceRules.isBiome(
            AlmondForestRegion.ALMOND_FOREST
        );

        // Surface rules for Almond Forest:
        // - Grass block on top
        // - Dirt underneath
        SurfaceRules.RuleSource almondForestSurface = SurfaceRules.sequence(
            // Floor surface (top block)
            SurfaceRules.ifTrue(
                SurfaceRules.ON_FLOOR,
                SurfaceRules.ifTrue(
                    SurfaceRules.waterBlockCheck(-1, 0),
                    GRASS_BLOCK
                )
            ),
            // Under floor (subsurface dirt)
            SurfaceRules.ifTrue(
                SurfaceRules.UNDER_FLOOR,
                DIRT
            )
        );

        // Apply rules only to Almond Forest biome
        return SurfaceRules.ifTrue(isAlmondForest, almondForestSurface);
    }
}
