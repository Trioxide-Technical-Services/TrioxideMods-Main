package com.thewoodlands.block;

import com.thewoodlands.TheWoodlands;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TheWoodlands.MOD_ID);

    public static final RegistryObject<WoodPortalBlock> WOOD_PORTAL =
            BLOCKS.register("wood_portal", WoodPortalBlock::new);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
