package com.robertx22.library_of_exile.dimension.worlddata;

import net.minecraft.world.level.ChunkPos;

public class MapStructureCounter {

    // map instances used to be handed out in a single line along +Z, so the Nth map sat N spacings out
    // and long lived servers crawled towards the world border. walking a band instead grows Z 64x slower.
    private static final int BAND = 64;

    private int x = 0;
    private int z = 0;

    public ChunkPos getNextAndIncrement() {
        // old saves are at x=1, z=count. resuming from there yields (2,count)..(BAND,count) then
        // (0,count+1),(1,count+1).. so an already handed out (1, 0..count) is never given out twice.
        if (x >= BAND) {
            x = 0;
            z++;
        } else {
            x++;
        }
        return new ChunkPos(x, z);
    }
}
