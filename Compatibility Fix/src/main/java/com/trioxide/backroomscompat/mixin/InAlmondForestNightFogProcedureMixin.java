package com.trioxide.backroomscompat.mixin;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Mixin to make the fog procedure also recognize our biome
 */
@Mixin(targets = "net.whyantique.faithfulbackrooms.procedures.InAlmondForestNightFogProcedure", remap = false)
public class InAlmondForestNightFogProcedureMixin {

    private static final ResourceLocation OUR_BIOME = new ResourceLocation("backrooms_terralith_compat", "almond_forest");
    private static final ResourceLocation ORIGINAL_BIOME = new ResourceLocation("faithfulbackrooms", "almond_forest");

    /**
     * Intercept ResourceLocation comparisons and also match our biome
     */
    @ModifyArg(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;equals(Ljava/lang/Object;)Z"),
        index = 0,
        require = 0
    )
    private static Object modifyBiomeCheck(Object original) {
        if (original instanceof ResourceLocation rl && rl.equals(OUR_BIOME)) {
            // If checking against our biome, return the original biome so it matches
            return ORIGINAL_BIOME;
        }
        return original;
    }
}
