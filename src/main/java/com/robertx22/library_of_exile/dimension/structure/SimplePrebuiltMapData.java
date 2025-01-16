package com.robertx22.library_of_exile.dimension.structure;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;

// simple prebuilt map assuming a size * size 2d map of 1 chunk rooms
public class SimplePrebuiltMapData {

    public int size;
    // only the folder is typed, so it could be modid:rooms/1_1 , but you dont type the 1_1
    public String rooms_folder = "";

    public SimplePrebuiltMapData(int size, String rooms_folder) {
        this.size = size;
        this.rooms_folder = rooms_folder;
    }

    public ResourceLocation getRoomForChunk(ChunkPos pos, MapStructure struc) {
        try {
            ChunkPos relative = struc.getRelativeChunkPosFromStart(pos);
            int x = relative.x;
            int z = relative.z;

            if (isWithinBounds(x, z)) {
                return new ResourceLocation(rooms_folder + "/" + x + "_" + z);
            }
        } catch (Exception e) {
        }
        return null;
    }


    public boolean isWithinBounds(int x, int z) {
        int ax = Math.abs(x);
        int az = Math.abs(z);
        return ax <= size && az <= size;
    }
}
