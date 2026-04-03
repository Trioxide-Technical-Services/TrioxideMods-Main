package com.thewoodlands.event;

import com.thewoodlands.dimension.ModDimensions;
import com.thewoodlands.entity.SpiderBossEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "thewoodlands", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpiderBossEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        // Only care about player deaths in Layer 2
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!ModDimensions.WOODLANDS_2.equals(player.level().dimension())) return;

        // Only if killed by the Spider Boss
        var source = event.getSource();
        if (!(source.getEntity() instanceof SpiderBossEntity)) return;

        // Cancel the normal death — no death screen, no respawn prompt yet
        event.setCanceled(true);

        // Instant Wood Person transformation instead
        ModEventHandler.transformIntoWoodPerson(player);
    }
}
