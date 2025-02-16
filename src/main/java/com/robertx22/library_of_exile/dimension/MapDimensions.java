package com.robertx22.library_of_exile.dimension;

import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class MapDimensions {

    // this way I can keep track of what dimensions are maps
    private static HashMap<String, MapDimensionInfo> map = new HashMap<>();


    public static boolean isMap(ResourceLocation id) {
        boolean is = map.getOrDefault(id.toString(), null) != null;
        return is;
    }

    public static boolean isMap(Level world) {
        var dim = world.dimensionTypeId().location();
        boolean is = isMap(dim);
        return is;
    }

    public static MapDimensionInfo getInfo(ResourceLocation id) {
        return map.get(id.toString());
    }

    public static MapDimensionInfo getInfo(MapStructure s) {
        return map.get(s.guid());
    }

    public static MapDimensionInfo getInfo(Level world) {
        return getInfo(world.dimensionTypeId().location());
    }

    public static void register(MapDimensionInfo info) {
        map.put(info.dimensionId.toString(), info);
    }
}
