package com.robertx22.library_of_exile.components;

import com.robertx22.library_of_exile.config.map_dimension.ChunkProcessType;
import com.robertx22.library_of_exile.dimension.structure.MapStructure;

import java.util.HashMap;
import java.util.List;

public class MapChunkData {

    private HashMap<String, Boolean> gen = new HashMap<>();

    public HashMap<ChunkProcessType, List<BlockData>> mapBlocks = new HashMap<>();

    public void setGeneratedData(MapStructure struc) {
        gen.put(struc.guid(), true);
    }

    public boolean generatedData(MapStructure s) {
        return gen.getOrDefault(s.guid(), false);
    }

    /**
     * Puts the chunk back in the state a freshly generated one is in, so the normal per player
     * processing rescans it. Needed after a chunk is re-carved: the flag was already set while the
     * chunk was still solid bedrock, and nothing would ever look at the data blocks placed since.
     */
    public void clearGeneratedData() {
        gen.clear();
    }
}
