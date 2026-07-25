package com.robertx22.library_of_exile.dimension.structure.dungeon;

import com.robertx22.library_of_exile.config.map_dimension.ProcessMapChunks;
import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

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
    //
    // resolving WHICH dungeon belongs here is cheap, building its grid is not, so the cached grid is
    // kept only while it still belongs to that dungeon. it's re-resolved every call because the answer
    // can change under us: the first chunk of a brand new instance can be generated in the window
    // before its map data is readable, and a layout built from that guess must not become the
    // instance's identity - that's what made a "Warped" map generate as an entirely different dungeon.
    public BuiltDungeon getBuiltDungeon(ChunkPos start) {
        return getBuiltDungeon(start, getMap(start));
    }

    /**
     * @param resolved a builder already resolved for this start chunk. Generation resolves one per chunk
     *                 anyway, so passing it in keeps this to a cache lookup instead of a second
     *                 (list-copying) getMap call per chunk.
     */
    public BuiltDungeon getBuiltDungeon(ChunkPos start, DungeonBuilder resolved) {

        if (!resolved.resolvedFromMapData) {
            // a guess. never cache it, and drop any cached guess so the next caller that CAN see the
            // map data replaces it instead of inheriting this one.
            builtDungeonCache.remove(start);
            resolved.build();
            return resolved.builtDungeon;
        }

        var cached = builtDungeonCache.get(start);
        if (cached != null) {
            if (isSameDungeon(cached, resolved)) {
                return cached;
            }
            // cached grid belongs to a different dungeon than this instance actually is - it was built
            // from a guess made before the map data was readable. drop it and build the real one.
            builtDungeonCache.remove(start);
        }

        // computeIfAbsent, not put: it runs under the wrapper's mutex, so the first thread to enter a
        // new instance builds the grid once instead of every worldgen thread building its own copy.
        return builtDungeonCache.computeIfAbsent(start, k -> {
            resolved.build();
            return resolved.builtDungeon;
        });
    }

    private static boolean isSameDungeon(BuiltDungeon cached, DungeonBuilder resolved) {
        if (cached.b == null || cached.b.dungeon == null || resolved.dungeon == null) {
            return false;
        }
        return cached.b.dungeon.GUID().equals(resolved.dungeon.GUID());
    }

    /**
     * Finds a data block inside the room whose origin chunk is {@code originChunk}, without the
     * dimension having generated anything. The layout is deterministic from the start chunk, so the
     * room's template and rotation are already known and its data blocks can be located by reading
     * the template directly. Needed because the spawn position has to be picked when a map is
     * created, which is before any of its chunks exist.
     *
     * @param matches        tested against each data block's string (command block command / structure block metadata)
     * @param preferClosestTo if several blocks match, the nearest one to this wins
     */
    public Optional<BlockPos> findDataBlockInRoom(MinecraftServer server, ChunkPos originChunk, Predicate<String> matches, BlockPos preferClosestTo) {
        try {
            var built = getBuiltDungeon(getStartChunkPos(originChunk));
            if (built == null) {
                return Optional.empty();
            }
            BuiltRoom room = built.getRoomForChunk(this, originChunk);
            if (room == null || room.room.isBarrier) {
                return Optional.empty();
            }
            var opt = server.getStructureManager().get(room.getStructure());
            if (opt.isEmpty()) {
                return Optional.empty();
            }
            var template = opt.get();

            BlockPos anchor = originChunk.getBlockAt(0, getSpawnHeight(), 0);
            Rotation rota = room.data.rotation;

            BlockPos best = null;
            double bestDist = Double.MAX_VALUE;

            for (Block type : Arrays.asList(Blocks.COMMAND_BLOCK, Blocks.STRUCTURE_BLOCK)) {
                for (var block : template.filterBlocks(BlockPos.ZERO, new StructurePlaceSettings(), type, true)) {
                    if (!matches.test(ProcessMapChunks.getDataString(block))) {
                        continue;
                    }
                    BlockPos world = DungeonRoomPlacer.dataBlockWorldPos(anchor, rota, template, block.pos());
                    double dist = world.distSqr(preferClosestTo);
                    if (dist < bestDist) {
                        bestDist = dist;
                        best = world;
                    }
                }
            }
            return Optional.ofNullable(best);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean generateInChunk(ServerLevelAccessor level, StructureTemplateManager man, ChunkPos cpos) {
        var start = getStartChunkPos(cpos);
        var data = getMap(start);
        return DungeonRoomPlacer.generateStructure(this, data, level, cpos);
    }
}
