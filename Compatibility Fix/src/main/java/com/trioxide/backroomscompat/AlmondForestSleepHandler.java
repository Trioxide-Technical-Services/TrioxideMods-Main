package com.trioxide.backroomscompat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = BackroomsCompatMod.MOD_ID)
public class AlmondForestSleepHandler {

    private static final ResourceLocation OUR_ALMOND_FOREST = new ResourceLocation("backrooms_terralith_compat", "almond_forest");
    private static final ResourceLocation ALMOND_DRAWER = new ResourceLocation("faithfulbackrooms", "almond_drawer");
    private static final ResourceKey<Level> LEVEL_0 = ResourceKey.create(
        net.minecraft.core.registries.Registries.DIMENSION,
        new ResourceLocation("faithfulbackrooms", "level_0")
    );

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            BlockPos bedPos = player.getSleepingPos().orElse(player.blockPosition());

            // Check if player is in our almond forest biome
            Holder<Biome> biomeHolder = level.getBiome(bedPos);
            ResourceLocation biomeName = biomeHolder.unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);

            if (!OUR_ALMOND_FOREST.equals(biomeName)) {
                return;
            }

            // Check if it was night (player slept through the night, not just got in bed)
            // If wakeImmediately is false, they completed the sleep cycle
            if (event.wakeImmediately()) {
                return; // They were woken up early, don't teleport
            }

            // Check for almond drawer adjacent to bed
            if (!hasAdjacentAlmondDrawer(level, bedPos)) {
                return;
            }

            // Teleport to level_0
            ServerLevel backroomsLevel = level.getServer().getLevel(LEVEL_0);
            if (backroomsLevel != null) {
                player.teleportTo(backroomsLevel, 0, 1, 0, player.getYRot(), player.getXRot());
            }
        }
    }

    private static boolean hasAdjacentAlmondDrawer(ServerLevel level, BlockPos bedPos) {
        // Check all adjacent positions (including diagonals and above/below)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    BlockPos checkPos = bedPos.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(checkPos);
                    ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());

                    if (ALMOND_DRAWER.equals(blockId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
