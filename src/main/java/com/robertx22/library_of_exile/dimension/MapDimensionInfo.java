package com.robertx22.library_of_exile.dimension;

import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import net.minecraft.resources.ResourceLocation;

public class MapDimensionInfo {

    public ResourceLocation dimensionId;
    public MapStructure structure;

    public MapDimensionInfo(ResourceLocation dimensionId, MapStructure structure) {
        this.dimensionId = dimensionId;
        this.structure = structure;
    }
}
