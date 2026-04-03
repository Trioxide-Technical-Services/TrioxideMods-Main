package com.thewoodlands;

import com.thewoodlands.dimension.ModDimensions;
import com.thewoodlands.entity.ModEntities;
import com.thewoodlands.entity.WoodPersonEntity;
import com.thewoodlands.world.ModChunkGenerators;
import com.thewoodlands.event.ModEventHandler;
import com.thewoodlands.event.WoodPersonEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TheWoodlands.MOD_ID)
public class TheWoodlands {
    public static final String MOD_ID = "thewoodlands";
    public static final Logger LOGGER = LogManager.getLogger();

    public TheWoodlands() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEntities.register(modEventBus);
        ModDimensions.register(modEventBus);
        ModChunkGenerators.register(modEventBus);
        com.thewoodlands.block.ModBlocks.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(ModEventHandler.class);
        MinecraftForge.EVENT_BUS.register(WoodPersonEvents.class);
        MinecraftForge.EVENT_BUS.register(com.thewoodlands.event.LoreBookEvents.class);
        MinecraftForge.EVENT_BUS.register(com.thewoodlands.event.PortalEvents.class);
        MinecraftForge.EVENT_BUS.register(com.thewoodlands.event.SpiderBossEvents.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("[TheWoodlands] The forest is watching...");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("[TheWoodlands] Client initialised. Don't go into the woods.");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
