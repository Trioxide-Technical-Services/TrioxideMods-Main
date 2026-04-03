package com.thewoodlands.entity;

import com.thewoodlands.TheWoodlands;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TheWoodlands.MOD_ID);

    public static final RegistryObject<EntityType<WoodPersonEntity>> WOOD_PERSON =
            ENTITY_TYPES.register("wood_person", () ->
                    EntityType.Builder.<WoodPersonEntity>of(WoodPersonEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(64)
                            .updateInterval(3)
                            .build(TheWoodlands.MOD_ID + ":wood_person")
            );

    public static final RegistryObject<EntityType<SpiderBossEntity>> SPIDER_BOSS =
            ENTITY_TYPES.register("spider_boss", () ->
                    EntityType.Builder.<SpiderBossEntity>of(SpiderBossEntity::new, MobCategory.MONSTER)
                            .sized(2.4f, 2.0f)        // Very large — wide and imposing
                            .clientTrackingRange(80)
                            .updateInterval(3)
                            .build(TheWoodlands.MOD_ID + ":spider_boss")
            );

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
