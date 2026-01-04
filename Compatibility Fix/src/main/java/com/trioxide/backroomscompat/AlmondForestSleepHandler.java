package com.trioxide.backroomscompat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BackroomsCompatMod.MOD_ID)
public class AlmondForestSleepHandler {

    private static final ResourceLocation OUR_ALMOND_FOREST = new ResourceLocation("backrooms_terralith_compat", "almond_forest");
    private static final ResourceKey<Level> LEVEL_0 = ResourceKey.create(
        net.minecraft.core.registries.Registries.DIMENSION,
        new ResourceLocation("faithfulbackrooms", "level_0")
    );

    @SubscribeEvent
    public static void onPlayerSleep(PlayerSleepInBedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            BlockPos pos = player.blockPosition();

            // Check if player is in our almond forest biome
            Holder<Biome> biomeHolder = level.getBiome(pos);
            ResourceLocation biomeName = biomeHolder.unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);

            if (OUR_ALMOND_FOREST.equals(biomeName)) {
                // Get or create level_0 dimension
                ServerLevel backroomsLevel = level.getServer().getLevel(LEVEL_0);
                if (backroomsLevel != null) {
                    // Schedule teleport for next tick (after sleep animation starts)
                    player.getServer().execute(() -> {
                        // Teleport player to level_0 at a safe position
                        player.teleportTo(backroomsLevel, 0, 1, 0, player.getYRot(), player.getXRot());
                    });
                }
            }
        }
    }
}
