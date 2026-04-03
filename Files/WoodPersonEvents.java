package com.thewoodlands.event;

import com.thewoodlands.entity.ModEntities;
import com.thewoodlands.entity.SpiderBossEntity;
import com.thewoodlands.entity.WoodPersonEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "thewoodlands", bus = Mod.EventBusSubscriber.Bus.MOD)
public class WoodPersonEvents {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.WOOD_PERSON.get(), WoodPersonEntity.createAttributes().build());
        event.put(ModEntities.SPIDER_BOSS.get(), SpiderBossEntity.createAttributes().build());
    }
}
