package com.thewoodlands.world;

import com.thewoodlands.TheWoodlands;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModChunkGenerators {

    public static final DeferredRegister<com.mojang.serialization.Codec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, TheWoodlands.MOD_ID);

    public static final RegistryObject<com.mojang.serialization.Codec<? extends ChunkGenerator>> WOODLANDS_GENERATOR =
            CHUNK_GENERATORS.register("woodlands", () -> WoodlandsChunkGenerator.CODEC);

    public static void register(IEventBus bus) {
        CHUNK_GENERATORS.register(bus);
    }
}
