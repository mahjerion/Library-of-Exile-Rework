package com.robertx22.library_of_exile.dimension.worlddata;

import com.robertx22.library_of_exile.components.AllMapConnectionData;
import com.robertx22.library_of_exile.components.MapConnectionsCap;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;

// todo bug test this, it's a bit confusing code-wise
public class MapPlayerDataSaver<T> {

    // x_z pos - map data map
    private HashMap<String, T> map = new HashMap<String, T>();
    // player UUID - x_z pos map
    private HashMap<String, String> playerMapIdMap = new HashMap<>();

    public T getData(Player p) {
        String id = p.getStringUUID();
        return map.get(id);
    }

    public T getData(MapStructure structure, BlockPos pos) {
        var start = structure.getStartChunkPos(new ChunkPos(pos));
        String key = getKey(start);
        return map.get(key);
    }

    public boolean hasData(Player p) {
        return map.containsKey(p.getStringUUID());
    }

    public boolean hasData(MapStructure structure, BlockPos pos) {
        var start = structure.getStartChunkPos(new ChunkPos(pos));
        String key = getKey(start);
        return map.containsKey(key);
    }

    //can connect 2 maps with this here?
    public void setData(Player p, T data, MapStructure structure, BlockPos pos) {

        // remove the old player map data
        if (playerMapIdMap.containsKey(p.getStringUUID())) {
            map.remove(playerMapIdMap.get(p.getStringUUID()));
        }

        var start = structure.getStartChunkPos(new ChunkPos(pos));
        String key = getKey(start);
        map.put(key, data);
        playerMapIdMap.put(p.getStringUUID(), key);

        // map connections
        AllMapConnectionData cons = MapConnectionsCap.get(p.level()).data;
        var origin = MapDimensions.getInfo(p.level());
        if (origin != null) {
            var side = MapDimensions.getInfo(structure);
            cons.tryCreateConnection(origin, p.blockPosition(), side, pos);
        }
    }

    public String getKey(ChunkPos cp) {
        return cp.x + "_" + cp.z;
    }
}
