package com.robertx22.library_of_exile.dimension.structure;

import com.robertx22.library_of_exile.components.LibChunkCap;
import com.robertx22.library_of_exile.dimension.MapGenerationUTIL;
import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SimplePrebuiltMapStructure extends MapStructure<SimplePrebuiltMapData> {


    @Override
    public boolean generateInChunk(ServerLevelAccessor level, StructureTemplateManager man, ChunkPos cpos) {

        var start = getStartChunkPos(cpos);
        var pieces = getMap(start);

        // learn the map's real extent before asking it for a room. without this, a rectangular arena
        // gets asked for a whole row of templates that were never meant to exist, and every one of
        // them reports itself missing.
        pieces.resolveFootprint(man);

        var room = pieces.getRoomForChunk(cpos, this);
        if (room != null) {
            return MapGenerationUTIL.spawnStructure(level, cpos, man, getSpawnHeight(), room);
        }
        return true;
    }

    @Override
    public int repairChunksAround(ServerLevel level, Collection<ChunkPos> chunks) {
        int repaired = 0;
        StructureTemplateManager man = level.getServer().getStructureManager();

        // group by instance first - see DungeonStructure.repairChunksAround. getMap is a lookup
        // through a synchronized LRU for the arenas, and getRoomForChunk builds a ResourceLocation
        // from string concatenation, so doing either per chunk is pure waste when a whole process
        // radius almost always belongs to one instance.
        Map<ChunkPos, List<ChunkPos>> byInstance = new HashMap<>();
        for (ChunkPos cpos : chunks) {
            // never let the repair be the thing that generates a chunk - that is the hang this whole
            // change exists to stop.
            if (level.hasChunk(cpos.x, cpos.z)) {
                byInstance.computeIfAbsent(getStartChunkPos(cpos), k -> new ArrayList<>()).add(cpos);
            }
        }

        for (var entry : byInstance.entrySet()) {
            // per instance, so one broken instance can't skip the repair for everyone else in the batch
            try {
                // resolved lazily, on the first chunk that actually needs carving - see the same
                // comment in DungeonStructure. This runs on every map chunk load, and an already carved
                // chunk must cost one block read and nothing else.
                SimplePrebuiltMapData map = null;
                boolean instanceResolved = false;

                for (ChunkPos cpos : entry.getValue()) {
                    if (repairFailedBefore(cpos)) {
                        continue;
                    }
                    if (!isUncarved(level, cpos)) {
                        continue;
                    }

                    if (!instanceResolved) {
                        instanceResolved = true;
                        map = getMap(entry.getKey());
                        if (map == null) {
                            break;
                        }
                        map.resolveFootprint(man);
                    }

                    if (repairChunk(level, cpos, map, man)) {
                        repaired++;
                    }
                }
            } catch (Exception e) {
                ExileLog.get().error("Failed while repairing chunks of '" + guid() + "' at instance "
                        + entry.getKey() + ".", e);
            }
        }
        return repaired;
    }

    /**
     * Re-carves any chunk of this structure's footprint that generation left as solid bedrock, and
     * returns how many it repaired.
     * <p>
     * A map chunk is born as solid bedrock and is offered to the structures exactly once, when
     * {@code buildSurface} runs. If that carve is skipped - a structure earlier in the chunk
     * generation event threw, a template was missing, worldgen hit an exception - the chunk stays
     * bedrock forever, because Minecraft never calls {@code buildSurface} on an already generated
     * chunk. On a server that recycles instance coordinates, the next group to be handed those
     * coordinates inherits the same hole.
     * <p>
     * Meant to be called from the explicit, rare action of teleporting a player into the structure,
     * so it costs nothing per tick.
     */
    public int repairMissingChunks(ServerLevel level, ChunkPos anyChunkOfIt) {
        try {
            ChunkPos start = getStartChunkPos(anyChunkOfIt);
            SimplePrebuiltMapData map = getMap(start);
            if (map == null) {
                return 0;
            }

            StructureTemplateManager man = level.getServer().getStructureManager();
            map.resolveFootprint(man);

            int repaired = 0;

            // the resolved footprint, not the declared size - chunks beyond the last real room are
            // meant to be bedrock, and probing them would repeat the phantom "missing room" reports
            // on every single arena entry.
            for (int x = 0; x <= map.maxRoomX(); x++) {
                for (int z = 0; z <= map.maxRoomZ(); z++) {

                    ChunkPos cpos = new ChunkPos(start.x + x, start.z + z);
                    // repairChunk deliberately does not test these itself - the chunk load path has to
                    // do them BEFORE resolving the instance, so they live with the caller.
                    if (repairFailedBefore(cpos) || !isUncarved(level, cpos)) {
                        continue;
                    }
                    if (repairChunk(level, cpos, map, man)) {
                        repaired++;
                    }
                }
            }
            return repaired;

        } catch (Exception e) {
            ExileLog.get().error("Failed while checking '" + guid() + "' near " + anyChunkOfIt + " for un-carved chunks.", e);
            return 0;
        }
    }

    /**
     * Re-carves one chunk generation left as bedrock. The caller owns the loaded check, the
     * {@code repairFailedBefore} check and the {@code isUncarved} check.
     */
    private boolean repairChunk(ServerLevel level, ChunkPos cpos, SimplePrebuiltMapData map, StructureTemplateManager man) {
        var room = map.getRoomForChunk(cpos, this);
        if (room == null) {
            // outside the resolved footprint - this chunk is meant to be bedrock
            return false;
        }
        // spawnStructure reports a missing or oversized template itself. nothing can be done about
        // those here - there is no template to place - so don't count it, and don't come back.
        //
        // knownShape = true: unlike generation, this runs on the server thread against a live world,
        // where vanilla's edge fixup would read and write the neighbouring chunk and block until it
        // loaded. See the parameter's doc on spawnStructure.
        if (!MapGenerationUTIL.spawnStructure(level, cpos, man, getSpawnHeight(), room, true)) {
            rememberRepairFailure(cpos);
            return false;
        }

        // the chunk was already marked processed while it was still bedrock, so without this the data
        // blocks just placed would never be looked at and the room would be an empty shell with no
        // mobs, chests or teleporters.
        level.getChunk(cpos.x, cpos.z).getCapability(LibChunkCap.INSTANCE)
                .ifPresent(cap -> cap.mapGenData.clearGeneratedData());

        // deliberately loud. this firing is the evidence that generation dropped a chunk, which is the
        // thing that has been invisible in server logs.
        ExileLog.get().warn("Repaired an un-carved chunk of '" + guid() + "' at " + cpos
                + " (instance start " + getStartChunkPos(cpos) + ", room " + room + "). It was solid bedrock,"
                + " meaning chunk generation never placed this room. Re-placed it now.");
        return true;
    }
}
