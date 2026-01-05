package com.trioxide.backroomscompat;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

@Mod(BackroomsCompatMod.MOD_ID)
public class BackroomsCompatMod {
    public static final String MOD_ID = "backrooms_terralith_compat";
    public static final Logger LOGGER = LogManager.getLogger();

    public BackroomsCompatMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Backrooms-Terralith Compatibility Mod initialized");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Register our region with TerraBlender (weight of 2 = rare but findable)
            Regions.register(new AlmondForestRegion(
                new ResourceLocation(MOD_ID, "almond_forest"),
                2
            ));

            // Register surface rules for proper terrain generation
            SurfaceRuleManager.addSurfaceRules(
                SurfaceRuleManager.RuleCategory.OVERWORLD,
                MOD_ID,
                AlmondForestSurfaceRules.makeRules()
            );

            LOGGER.info("Registered Almond Forest region and surface rules with TerraBlender");
        });
    }
}
