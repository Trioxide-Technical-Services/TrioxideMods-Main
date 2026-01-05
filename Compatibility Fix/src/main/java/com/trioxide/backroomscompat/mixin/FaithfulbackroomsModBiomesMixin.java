package com.trioxide.backroomscompat.mixin;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to prevent the Backrooms mod from injecting its almond_forest biome
 * into the overworld. We use our own TerraBlender-based placement instead
 * which properly places the biome at surface level.
 */
@Mixin(targets = "net.whyantique.faithfulbackrooms.init.FaithfulbackroomsModBiomes", remap = false)
public class FaithfulbackroomsModBiomesMixin {

    /**
     * Cancel the server startup biome injection entirely.
     * The Backrooms mod adds almond_forest to the overworld here with
     * climate parameters that cause it to spawn underground.
     * We disable this and use TerraBlender instead.
     */
    @Inject(
        method = "onServerAboutToStart",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private static void cancelAlmondForestInjection(ServerAboutToStartEvent event, CallbackInfo ci) {
        // Cancel the entire method to prevent underground biome spawning
        ci.cancel();
    }
}
