package com.robertx22.library_of_exile.dimension;

import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;

public class MapDimensionInfo {

    public ResourceLocation dimensionId;
    public MapStructure structure;
    public MapContentType contentType = MapContentType.SIDE_CONTENT;
    public List<MapStructure> secondaryStructures = new ArrayList<>();


    public MapDimensionInfo(ResourceLocation dimensionId, MapStructure structure, MapContentType contentType, List<MapStructure> secondaryStructures) {
        this.dimensionId = dimensionId;
        this.structure = structure;
        this.contentType = contentType;
        this.secondaryStructures = secondaryStructures;
    }

    public boolean isInside(MapStructure struc, ServerLevel level, BlockPos pos) {

        if (!level.dimension().location().equals(dimensionId)) {
            return false;
        }
        if (!struc.isInside(level, pos)) {
            return false;
        }
        return true;
    }

    public boolean hasStructure(MapStructure struc) {
        if (struc.guid().equals(structure.guid())) {
            return true;
        }
        return secondaryStructures.stream().anyMatch(x -> x.guid().equals(struc.guid()));
    }


}
