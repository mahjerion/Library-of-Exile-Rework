package com.robertx22.library_of_exile.dimension;

import com.robertx22.library_of_exile.events.base.ExileEvent;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class MapChunkGenEvent extends ExileEvent {

    public RandomState ran;
    public StructureTemplateManager manager;
    public ChunkAccess chunk;
    public WorldGenRegion world;

    public MapChunkGenEvent(RandomState ran, StructureTemplateManager manager, ChunkAccess chunk, WorldGenRegion world, String mapId) {
        this.ran = ran;
        this.manager = manager;
        this.chunk = chunk;
        this.world = world;
        this.mapId = mapId;
    }

    public String mapId;


}
