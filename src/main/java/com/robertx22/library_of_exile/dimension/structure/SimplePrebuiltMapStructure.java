package com.robertx22.library_of_exile.dimension.structure;

import com.robertx22.library_of_exile.components.LibChunkCap;
import com.robertx22.library_of_exile.dimension.MapGenerationUTIL;
import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

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

    // columns sampled to decide whether a chunk was ever carved. the chunk's own centre plus the four
    // quadrant centres - a room a player can stand in has air somewhere in at least one of them.
    private static final int[][] PROBE_COLUMNS = {{8, 8}, {4, 4}, {12, 4}, {4, 12}, {12, 12}};

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

                    if (!isUncarved(level, cpos)) {
                        continue;
                    }
                    var room = map.getRoomForChunk(cpos, this);
                    if (room == null) {
                        continue;
                    }
                    // spawnStructure reports a missing or oversized template itself. nothing can be
                    // done about those here - there is no template to place - so just don't count it.
                    if (!MapGenerationUTIL.spawnStructure(level, cpos, man, getSpawnHeight(), room)) {
                        continue;
                    }

                    // the chunk was already marked processed while it was still bedrock, so without
                    // this the data blocks just placed would never be looked at and the room would be
                    // an empty shell with no mobs, chests or teleporters.
                    level.getChunk(cpos.x, cpos.z).getCapability(LibChunkCap.INSTANCE)
                            .ifPresent(cap -> cap.mapGenData.clearGeneratedData());

                    repaired++;

                    // deliberately loud. this firing is the evidence that generation dropped a chunk,
                    // which is the thing that has been invisible in server logs.
                    ExileLog.get().warn("Repaired an un-carved chunk of '" + guid() + "' at " + cpos
                            + " (instance start " + start + ", room " + room + "). It was solid bedrock, meaning"
                            + " chunk generation never placed this room. Re-placed it now.");
                }
            }
            return repaired;

        } catch (Exception e) {
            ExileLog.get().error("Failed while checking '" + guid() + "' near " + anyChunkOfIt + " for un-carved chunks.", e);
            return 0;
        }
    }

    /**
     * True only when every sampled block in the structure's height band is bedrock, which is exactly
     * the state {@code fillFromNoise} leaves a chunk in before anything carves it.
     * <p>
     * The all-or-nothing test is the point: it is what stops a repair from overwriting an arena
     * players have already fought through and dug into.
     */
    private boolean isUncarved(ServerLevel level, ChunkPos cpos) {
        var chunk = level.getChunk(cpos.x, cpos.z);

        int from = getSpawnHeight();
        int to = getSpawnHeight() + getStructureHeight();

        for (int[] col : PROBE_COLUMNS) {
            for (int y = from; y < to; y++) {
                if (!chunk.getBlockState(cpos.getBlockAt(col[0], y, col[1])).is(Blocks.BEDROCK)) {
                    return false;
                }
            }
        }
        return true;
    }
}
