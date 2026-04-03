package com.thewoodlands.dimension;

import com.thewoodlands.TheWoodlands;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModDimensions {

    // Layer 1 - The Woodlands: the infinite wooden labyrinth.
    //           Wood People are here but don't chase for 15 minutes.
    //           The Spider Boss roams. Getting touched sends you to Layer 2.
    public static final ResourceKey<Level> WOODLANDS_1 = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            new ResourceLocation(TheWoodlands.MOD_ID, "woodlands_1")
    );

    // Layer 2 - The Deep Wood: madness sets in. After enough time here,
    //           you become a Wood Person. Respawn is in the overworld.
    public static final ResourceKey<Level> WOODLANDS_2 = ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION,
            new ResourceLocation(TheWoodlands.MOD_ID, "woodlands_2")
    );

    public static void register(IEventBus bus) {
        // Dimensions registered via JSON
    }

    public static int getLayer(ResourceKey<Level> dimension) {
        if (dimension.equals(WOODLANDS_1)) return 1;
        if (dimension.equals(WOODLANDS_2)) return 2;
        return 0;
    }

    public static boolean isWoodlandsDimension(ResourceKey<Level> dimension) {
        return getLayer(dimension) > 0;
    }
}
