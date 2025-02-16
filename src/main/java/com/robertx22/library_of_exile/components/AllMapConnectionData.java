package com.robertx22.library_of_exile.components;

import com.robertx22.library_of_exile.dimension.MapContentType;
import com.robertx22.library_of_exile.dimension.MapDimensionInfo;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;

// this allows primary maps to connect to side content maps that spawn from them
public class AllMapConnectionData {

    // side content v original map
    private HashMap<String, String> map = new HashMap<>();


    public String createKey(MapDimensionInfo struc, BlockPos pos) {
        var c = struc.structure.getStartChunkPos(pos);
        return struc.dimensionId.toString() + "-" + c.x + "_" + c.z;
    }

    public Data getDataFromKey(String key) {
        try {
            var struc = MapDimensions.getInfo(new ResourceLocation(key.split("-")[0]));

            String cps = key.split("-")[1];

            int x = Integer.parseInt(cps.split("_")[0]);
            int z = Integer.parseInt(cps.split("_")[1]);

            if (struc == null || cps == null) {
                return null;
            }

            return new Data(struc, new ChunkPos(x, z));
        } catch (Exception e) {
            return null;
        }
    }

    public static class Data {
        public MapDimensionInfo struc;
        public ChunkPos cp;

        public Data(MapDimensionInfo struc, ChunkPos cp) {
            this.struc = struc;
            this.cp = cp;
        }
    }

    public void tryCreateConnection(MapDimensionInfo origin, BlockPos oPos, MapDimensionInfo side, BlockPos sPos) {

        if (origin == null || side == null) {
            return;
        }
        if (!origin.contentType.canSpawnSideContent) {
            //ExileLog.get().warn(origin.dimensionId.toString() + " can not create side content!");
            return;
        }
        if (side.contentType != MapContentType.SIDE_CONTENT) {
            //ExileLog.get().warn(side.dimensionId.toString() + " is not valid side content!");
            return;
        }
        var key = createKey(origin, oPos);
        var key2 = createKey(side, sPos);
        map.put(key2, key);

    }

    // todo use this to connect map stats to side content
    public Data getOriginalMap(Level world, BlockPos pos) {
        var struc = MapDimensions.getInfo(world);

        if (struc != null && struc.contentType == MapContentType.SIDE_CONTENT) {
            var key = createKey(struc, pos);
            if (map.containsKey(key)) {
                var data = getDataFromKey(map.get(key));
                if (data != null) {
                    return data;
                }
            }
        }
        return null;
    }
}

