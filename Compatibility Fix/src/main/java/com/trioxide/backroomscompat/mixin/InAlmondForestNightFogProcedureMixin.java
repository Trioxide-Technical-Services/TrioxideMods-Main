package com.trioxide.backroomscompat.mixin;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.whyantique.faithfulbackrooms.procedures.InAlmondForestNightFogProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = InAlmondForestNightFogProcedure.class, remap = false)
public class InAlmondForestNightFogProcedureMixin {

    private static final ResourceLocation OUR_BIOME = new ResourceLocation("backrooms_terralith_compat", "almond_forest");

    // Target using SRG name m_203373_ for Holder.is()
    @Redirect(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDD)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;m_203373_(Lnet/minecraft/resources/ResourceLocation;)Z", remap = false),
        require = 0
    )
    private static boolean redirectBiomeCheck(Holder<?> holder, ResourceLocation location) {
        // Check both the original biome AND our biome
        return holder.is(location) || holder.is(OUR_BIOME);
    }
}
