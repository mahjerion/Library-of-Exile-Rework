package com.robertx22.library_of_exile.components;

import com.robertx22.library_of_exile.dimension.structure.MapStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapChunkData {

    private HashMap<String, Boolean> gen = new HashMap<>();

    public List<BlockData> dataBlocks = new ArrayList<>();

    public void setGeneratedData(MapStructure struc) {
        gen.put(struc.guid(), true);
    }

    public boolean generatedData(MapStructure s) {
        return gen.getOrDefault(s.guid(), false);
    }
}
