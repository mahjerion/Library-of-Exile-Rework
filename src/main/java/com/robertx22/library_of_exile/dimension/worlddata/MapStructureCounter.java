package com.robertx22.library_of_exile.dimension.worlddata;

import net.minecraft.world.level.ChunkPos;

public class MapStructureCounter {

    private int x = 0;
    private int z = 0;

    public ChunkPos getNextAndIncrement() {
        if (x > 0) {
            z++;
        } else {
            x++;
        }
        return new ChunkPos(x, z);
    }
}
