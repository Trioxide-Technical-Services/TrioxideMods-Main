package com.trioxide.backroomscompat.mixin;

import com.trioxide.backroomscompat.client.AlmondForestFogHandler;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.whyantique.faithfulbackrooms.procedures.InAlmondForestNightDisplayProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = InAlmondForestNightDisplayProcedure.class, remap = false)
public class InAlmondForestNightDisplayProcedureMixin {

    private static final ResourceLocation OUR_BIOME = new ResourceLocation("backrooms_terralith_compat", "almond_forest");

    @Redirect(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;m_203373_(Lnet/minecraft/resources/ResourceLocation;)Z", remap = false),
        require = 0
    )
    private static boolean redirectBiomeCheck(Holder<?> holder, ResourceLocation location) {
        // Check both biomes OR if we're still fading out
        return holder.is(location) || holder.is(OUR_BIOME) || AlmondForestFogHandler.shouldApplyFogEffects();
    }
}
