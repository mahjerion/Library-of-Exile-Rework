package com.robertx22.library_of_exile.dimension.structure.dungeon;

import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class DungeonStructure extends MapStructure<DungeonBuilder> {

    @Override
    public boolean generateInChunk(ServerLevelAccessor level, StructureTemplateManager man, ChunkPos cpos) {
        var start = getStartChunkPos(cpos);
        var data = getMap(start);
        return DungeonRoomPlacer.generateStructure(this, data, level, cpos);
    }
}
