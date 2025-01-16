package com.robertx22.library_of_exile.dimension;

import com.mojang.serialization.Codec;
import com.robertx22.library_of_exile.events.base.EventConsumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MapChunkGens {

    public static void init() {

    }

    //Simple empty chunk gen all my small map-dimension mods can use
    // the map stuff is generated in the event
    public static void registerMapChunkGenerator(ResourceLocation id, EventConsumer<MapChunkGenEvent> e) {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        DeferredRegister<Codec<? extends ChunkGenerator>> DEF = DeferredRegister.create(Registries.CHUNK_GENERATOR, id.getNamespace());

        DEF.register(bus);
        
        RegistryObject<Codec<? extends ChunkGenerator>> CHUNK_GEN = DEF.register(id.getPath(), () -> MapChunkGenerator.CODEC);

        MapGenEvents.MAP_CHUNK_GEN.register(e);

    }
}
