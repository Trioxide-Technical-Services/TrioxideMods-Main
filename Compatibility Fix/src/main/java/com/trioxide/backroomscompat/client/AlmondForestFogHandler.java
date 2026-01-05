package com.trioxide.backroomscompat.client;

import com.trioxide.backroomscompat.BackroomsCompatMod;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-side handler for Almond Forest fog fade effects.
 * Tracks biome transitions and provides smooth fog fade-out when leaving the biome.
 * Also enhances fog intensity when inside the biome for a more atmospheric effect.
 */
@Mod.EventBusSubscriber(modid = BackroomsCompatMod.MOD_ID, value = Dist.CLIENT)
public class AlmondForestFogHandler {

    private static final ResourceLocation ORIGINAL_BIOME = new ResourceLocation("faithfulbackrooms", "almond_forest");
    private static final ResourceLocation OUR_BIOME = new ResourceLocation("backrooms_terralith_compat", "almond_forest");

    // Fade duration in ticks (20 ticks = 1 second, 60 ticks = 3 seconds for smooth fade)
    private static final int FADE_DURATION_TICKS = 60;

    // Fog distance multiplier when fully inside the biome (lower = denser/thicker fog)
    // 0.4 means fog starts at 40% of normal distance, making it significantly thicker
    private static final float FOG_DENSITY_MULTIPLIER = 0.4f;

    // Current fog intensity (1.0 = full fog, 0.0 = no fog)
    private static float fogIntensity = 0.0f;

    // Whether we're currently in the Almond Forest
    private static boolean inAlmondForest = false;

    // Fade direction: true = fading in, false = fading out
    private static boolean fadingIn = false;

    /**
     * Check if the player is in an Almond Forest biome (ours or the original).
     */
    private static boolean isInAlmondForest(Player player) {
        if (player == null || player.level() == null) return false;

        Holder<Biome> biomeHolder = player.level().getBiome(player.blockPosition());
        return biomeHolder.is(ORIGINAL_BIOME) || biomeHolder.is(OUR_BIOME);
    }

    /**
     * Called by mixins to check if fog effects should be active.
     * Returns true if we're in the biome OR if we're still fading out.
     */
    public static boolean shouldApplyFogEffects() {
        return fogIntensity > 0.01f;
    }

    /**
     * Returns the current fog intensity for blending.
     * 1.0 = full fog, 0.0 = no fog
     */
    public static float getFogIntensity() {
        return fogIntensity;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            fogIntensity = 0.0f;
            inAlmondForest = false;
            return;
        }

        boolean wasInBiome = inAlmondForest;
        inAlmondForest = isInAlmondForest(mc.player);

        // Check for biome transition
        if (inAlmondForest && !wasInBiome) {
            // Entered the biome - start fading in
            fadingIn = true;
        } else if (!inAlmondForest && wasInBiome) {
            // Left the biome - start fading out
            fadingIn = false;
        }

        // Update fog intensity
        float fadeStep = 1.0f / FADE_DURATION_TICKS;

        if (inAlmondForest) {
            // In biome - fade in to full intensity
            fogIntensity = Math.min(1.0f, fogIntensity + fadeStep);
        } else {
            // Outside biome - fade out
            fogIntensity = Math.max(0.0f, fogIntensity - fadeStep);
        }
    }

    /**
     * Modifies fog density based on our fade intensity.
     * This event is fired when fog is being rendered.
     * When inside biome: makes fog denser (closer)
     * When fading out: gradually pushes fog away
     */
    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        // Only modify if we have any fog effect active
        if (fogIntensity > 0.01f) {
            // Get current fog distances
            float nearPlane = event.getNearPlaneDistance();
            float farPlane = event.getFarPlaneDistance();

            // Calculate the effective multiplier based on fog intensity
            // At fogIntensity = 1.0 (fully in biome): use FOG_DENSITY_MULTIPLIER (denser fog)
            // At fogIntensity = 0.0 (fully out): push fog far away (invisible)
            // The transition interpolates between these states

            // When fully in biome, make fog denser by reducing distance
            // When fading out, increase distance to push fog away
            float denseMultiplier = FOG_DENSITY_MULTIPLIER; // Makes fog closer/thicker when in biome
            float fadeAwayMultiplier = 15.0f; // How far to push fog when leaving

            // Interpolate: at intensity 1.0 use dense, at intensity 0.0 use far away
            float effectiveMultiplier = denseMultiplier + (1.0f - fogIntensity) * (fadeAwayMultiplier - denseMultiplier);

            event.setNearPlaneDistance(nearPlane * effectiveMultiplier);
            event.setFarPlaneDistance(farPlane * effectiveMultiplier);
            event.setCanceled(true);
        }
    }
}
