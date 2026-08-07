package com.robertx22.library_of_exile.dimension.structure;

import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

// simple prebuilt map assuming a size * size 2d map of 1 chunk rooms
public class SimplePrebuiltMapData {

    public int size;
    // only the folder is typed, so it could be modid:rooms/1_1 , but you dont type the 1_1
    public String rooms_folder = "";
    public int teleport_offset_x;
    public int teleport_offset_y;
    public int teleport_offset_z;

    public SimplePrebuiltMapData(int size, String rooms_folder) {
        this.size = size;
        this.rooms_folder = rooms_folder;
    }

    public ResourceLocation getRoomForChunk(ChunkPos pos, MapStructure struc) {
        try {
            ChunkPos relative = struc.getRelativeChunkPosFromStart(pos);
            if (relative.x < 0 || relative.z < 0) {
                return null;
            }

            int x = relative.x;
            int z = relative.z;

            if (isWithinBounds(x, z)) {
                return new ResourceLocation(rooms_folder + "/" + x + "_" + z);
            }
        } catch (Exception e) {
            ExileLog.get().error("Failed to resolve which room of '" + rooms_folder + "' belongs at " + pos
                    + ", so that chunk stays solid bedrock.", e);
        }
        return null;
    }

    /**
     * Every room this map actually has, as {@code x_z} template ids - the resolved footprint, not the
     * declared square. The startup check walks this, so a map that is legitimately rectangular reports
     * nothing and only a real hole inside the rectangle does.
     */
    public List<ResourceLocation> expectedRooms() {
        List<ResourceLocation> list = new ArrayList<>();
        for (int x = 0; x <= maxRoomX(); x++) {
            for (int z = 0; z <= maxRoomZ(); z++) {
                list.add(new ResourceLocation(rooms_folder + "/" + x + "_" + z));
            }
        }
        return list;
    }

    // The real extent of this map, worked out once from which templates exist rather than assumed
    // from `size`.
    //
    // `size` is only an upper bound on where to look. Arenas are authored as rectangles - the house
    // style is (size+1) wide by size deep - and a chunk outside the room set is SUPPOSED to be
    // bedrock: that's the wall around the arena, not a hole in it. Reading `size` as the exact extent
    // made the engine ask for a whole row of rooms that were never meant to exist, and report their
    // absence as damage.
    //
    // transient on purpose: this class is Gson serialized into the datapack JSON, and a persisted
    // field here would fail JsonExileRegistry's loaded-json-vs-class comparison. It also means a
    // /reload resets these, which is what we want - the next call re-resolves against the new pack.
    private transient volatile boolean footprintResolved = false;
    private transient volatile int footprintMaxX = -1;
    private transient volatile int footprintMaxZ = -1;

    /**
     * Works out the real extent from the templates that exist. Idempotent and cheap after the first
     * call - worldgen reaches this from several worker threads for one shared instance, so it resolves
     * once and is a plain volatile read from then on.
     */
    public void resolveFootprint(StructureTemplateManager man) {
        if (footprintResolved) {
            return;
        }
        synchronized (this) {
            if (footprintResolved) {
                return;
            }
            int maxX = -1;
            int maxZ = -1;
            for (int x = 0; x <= size; x++) {
                for (int z = 0; z <= size; z++) {
                    if (man.get(new ResourceLocation(rooms_folder + "/" + x + "_" + z)).isPresent()) {
                        maxX = Math.max(maxX, x);
                        maxZ = Math.max(maxZ, z);
                    }
                }
            }
            footprintMaxX = maxX;
            footprintMaxZ = maxZ;
            footprintResolved = true;
        }
    }

    /** True once {@link #resolveFootprint} has run and found at least one room. */
    public boolean hasResolvedFootprint() {
        return footprintResolved && footprintMaxX >= 0 && footprintMaxZ >= 0;
    }

    /** Largest room index in X, inclusive. Falls back to the declared {@link #size} until resolved. */
    public int maxRoomX() {
        return hasResolvedFootprint() ? footprintMaxX : size;
    }

    /** Largest room index in Z, inclusive. Falls back to the declared {@link #size} until resolved. */
    public int maxRoomZ() {
        return hasResolvedFootprint() ? footprintMaxZ : size;
    }

    public boolean isWithinBounds(int x, int z) {
        // no Math.abs: getRoomForChunk already rejects negatives, and abs would have mapped -1 onto
        // room 1. behaviour is unchanged for a square map, where maxRoomX/Z both equal size.
        return x >= 0 && z >= 0 && x <= maxRoomX() && z <= maxRoomZ();
    }
}
