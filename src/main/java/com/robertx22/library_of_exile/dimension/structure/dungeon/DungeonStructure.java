package com.robertx22.library_of_exile.dimension.structure.dungeon;

import com.robertx22.library_of_exile.components.LibChunkCap;
import com.robertx22.library_of_exile.config.map_dimension.ProcessMapChunks;
import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
            // a guess, and there is no right answer to give. This used to build the grid anyway, which
            // did two bad things: it cost a full 12-20 room build PER CHUNK (chunk generation is
            // already blocking a server thread by the time it gets here), and it wrote rooms of a
            // randomly rolled dungeon permanently into the world - a chunk is offered to the
            // structures exactly once, so the guess outlives the moment the real map data becomes
            // readable. That is how a 'nature' room ended up in the middle of a 'watcher' map.
            //
            // So refuse to answer. The chunk keeps the bedrock fillFromNoise left it as, and
            // repairChunksAround carves it for real once a caller can actually read the instance's
            // map data. Bedrock that becomes the right room beats a wrong room that is forever.
            builtDungeonCache.remove(start);
            return null;
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

    /**
     * The already-built grid for this instance, or null if nothing has built it yet. Never builds
     * one and never evicts.
     * <p>
     * For diagnostics that must not do work: {@code /report map_bug} is permission 0, and every other
     * entry point here either builds a grid or drops a cached one, so letting a player drive it is a
     * free way to make the server rebuild layouts on demand.
     */
    public BuiltDungeon getCachedDungeon(ChunkPos start) {
        return builtDungeonCache.get(start);
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
                    // null level: the dimension may not have generated anything yet, so the per-dimension
                    // opt-out can't be read here. A functional command block matches no data block id anyway.
                    if (!matches.test(ProcessMapChunks.getDataBlockKey(null, block))) {
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

    // one line per instance: repairChunksAround runs per player per second, so an instance that stays
    // unreadable would otherwise flood the log with the same sentence forever.
    private static final Set<ChunkPos> WARNED_SKIPPED_CARVE = ConcurrentHashMap.newKeySet();

    public static void forgetSkippedCarveWarnings() {
        WARNED_SKIPPED_CARVE.clear();
    }

    @Override
    public int repairChunksAround(ServerLevel level, Collection<ChunkPos> chunks) {
        int repaired = 0;

        // group by instance FIRST, so getMap and getBuiltDungeon are resolved once per instance
        // instead of once per chunk. That matters more than it looks: getMap allocates an
        // AtomicReference, a DungeonBuilder and a Random and copies a registry list, and
        // getBuiltDungeon takes the builtDungeonCache mutex - which worldgen threads hold for the
        // whole of a DungeonBuilder.build(). Doing that per chunk put ~49 acquisitions per player per
        // second on the server thread, on a lock a background thread can be sitting on. Instances are
        // DUNGEON_LENGTH chunks apart, so a process radius is almost always a single group anyway.
        Map<ChunkPos, List<ChunkPos>> byInstance = new HashMap<>();
        for (ChunkPos cpos : chunks) {
            // never let the repair be the thing that generates a chunk - that is the hang this whole
            // change exists to stop.
            if (level.hasChunk(cpos.x, cpos.z)) {
                byInstance.computeIfAbsent(getStartChunkPos(cpos), k -> new ArrayList<>()).add(cpos);
            }
        }

        for (var entry : byInstance.entrySet()) {
            ChunkPos start = entry.getKey();
            // per instance, so one broken instance can't skip the repair for everyone else in the batch
            try {
                // resolved lazily, on the first chunk that actually needs carving. This runs on every
                // map chunk load, and getMap allocates a builder + a Random + two registry copies while
                // getBuiltDungeon takes a mutex worldgen threads hold for whole dungeon builds. The
                // overwhelming majority of chunk loads are of already carved chunks, and those must not
                // pay any of that - isUncarved answers them in a single block read.
                boolean instanceResolved = false;
                DungeonBuilder resolved = null;
                BuiltDungeon built = null;
                int roomChunks = 1;

                for (ChunkPos cpos : entry.getValue()) {
                    if (repairFailedBefore(cpos)) {
                        // nothing placeable here, and finding that out again costs the full scan below
                        continue;
                    }
                    if (!isUncarved(level, cpos)) {
                        continue;
                    }

                    if (!instanceResolved) {
                        instanceResolved = true;
                        resolved = getMap(start);

                        if (resolved == null || !resolved.resolvedFromMapData) {
                            // still can't tell which dungeon belongs here, so there is still nothing
                            // safe to place. Say so once, then leave the instance alone.
                            if (WARNED_SKIPPED_CARVE.add(start)) {
                                ExileLog.get().warn("Not carving chunks of the dungeon instance at " + start
                                        + ": its map data is unreadable, so any room placed now would permanently"
                                        + " be the wrong dungeon. Leaving them as bedrock; they will be carved as"
                                        + " soon as the map data can be read.");
                            }
                            break;
                        }
                        built = getBuiltDungeon(start, resolved);
                        if (built == null) {
                            break;
                        }
                        roomChunks = built.b != null ? built.b.getRoomChunks() : resolved.getRoomChunks();
                    }

                    if (repairChunk(level, cpos, start, resolved, built, roomChunks)) {
                        repaired++;
                    }
                }
            } catch (Exception e) {
                ExileLog.get().error("Failed while repairing chunks of '" + guid() + "' at instance " + start + ".", e);
            }
        }
        return repaired;
    }

    /**
     * Re-carves one chunk generation left as bedrock. The caller owns the loaded check, the
     * {@code repairFailedBefore} check and the {@code isUncarved} check.
     */
    private boolean repairChunk(ServerLevel level, ChunkPos cpos, ChunkPos start, DungeonBuilder resolved,
                                BuiltDungeon built, int roomChunks) {
        if (built.getPlacementForChunk(this, cpos, roomChunks) == null) {
            // no cell of the grid covers this chunk - it is meant to be bedrock
            return false;
        }
        if (!DungeonRoomPlacer.generateStructure(this, resolved, level, cpos)) {
            rememberRepairFailure(cpos);
            return false;
        }

        // the chunk was already marked processed while it was still bedrock, so without this the data
        // blocks just placed would never be looked at and the room would be an empty shell with no
        // mobs, chests or teleporters.
        level.getChunk(cpos.x, cpos.z).getCapability(LibChunkCap.INSTANCE)
                .ifPresent(cap -> cap.mapGenData.clearGeneratedData());

        // deliberately loud. this firing is the evidence that generation dropped a chunk.
        ExileLog.get().warn("Repaired an un-carved chunk of '" + guid() + "' at " + cpos
                + " (instance start " + start + ", dungeon '" + (resolved.dungeon == null ? "?" : resolved.dungeon.GUID())
                + "'). It was solid bedrock, meaning chunk generation never placed this room.");
        return true;
    }
}
