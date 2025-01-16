package com.robertx22.library_of_exile.dimension.structure;

import com.robertx22.library_of_exile.dimension.MapGenerationUTIL;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class SimplePrebuiltMapStructure extends MapStructure<SimplePrebuiltMapData> {

    @Override
    public String guid() {
        return "simple_prebuilt_map";
    }

  
    @Override
    public boolean generateInChunk(ServerLevelAccessor level, StructureTemplateManager man, ChunkPos cpos) {

        var start = getStartChunkPos(cpos);
        var pieces = getMap(start);

        var room = pieces.getRoomForChunk(cpos, this);
        if (room != null) {
            return MapGenerationUTIL.spawnStructure(level, cpos, man, getHeight(), room);
        }
        return true;
    }
}
