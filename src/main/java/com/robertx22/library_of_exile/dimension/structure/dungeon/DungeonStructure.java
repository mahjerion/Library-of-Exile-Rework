package com.robertx22.library_of_exile.dimension.structure.dungeon;

import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class DungeonStructure extends MapStructure<DungeonBuilder> {

    private static final int MAX_CACHED_DUNGEONS = 32;

    // key = start ChunkPos of the dungeon instance, scoped per DungeonStructure singleton.
    // bounded because every instance a player ever brushes past would otherwise keep a full room grid
    // alive for the server's lifetime. evicting is safe, builds are deterministic from the start chunk
    // + world seed, so a rebuilt dungeon is identical. synchronizedMap because worldgen is threaded and
    // its computeIfAbsent runs under the wrapper's mutex.
    public final Map<ChunkPos, BuiltDungeon> builtDungeonCache = Collections.synchronizedMap(
            new LinkedHashMap<>(MAX_CACHED_DUNGEONS * 2, 0.75F, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<ChunkPos, BuiltDungeon> eldest) {
                    return size() > MAX_CACHED_DUNGEONS;
                }
            });

    // the built room grid for the instance at this start chunk. deterministic from start + world seed,
    // so a cache miss just rebuilds an identical grid. shared by generation and the map_bug report so
    // both see the exact same layout.
    public BuiltDungeon getBuiltDungeon(ChunkPos start) {
        return builtDungeonCache.computeIfAbsent(start, k -> {
            var b = getMap(start);
            b.build();
            return b.builtDungeon;
        });
    }

    @Override
    public boolean generateInChunk(ServerLevelAccessor level, StructureTemplateManager man, ChunkPos cpos) {
        var start = getStartChunkPos(cpos);
        var data = getMap(start);
        return DungeonRoomPlacer.generateStructure(this, data, level, cpos);
    }
}
