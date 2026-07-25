package com.robertx22.library_of_exile.dimension.worlddata;

import com.robertx22.library_of_exile.components.AllMapConnectionData;
import com.robertx22.library_of_exile.components.MapConnectionsCap;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// todo bug test this, it's a bit confusing code-wise
public class MapPlayerDataSaver<T> {

    public String uuid = UUID.randomUUID().toString();

    // x_z pos - map data map
    // concurrent, and it has to STAY declared as the concrete ConcurrentHashMap type: worldgen reads
    // this from background threads (getData, while a chunk is being generated) whereas setData writes
    // it on the server thread. a plain HashMap gives the reader no guarantee it ever observes the
    // write, and a missed read means the dungeon generates as something other than the map the player
    // started. declaring it as Map would compile but silently undo this - LoadSave rebuilds the field
    // with Gson, which picks the implementation from the declared type, so the world's first reload
    // would put a non concurrent map back.
    private ConcurrentHashMap<String, T> map = new ConcurrentHashMap<String, T>();
    // player UUID - x_z pos map
    private ConcurrentHashMap<String, String> playerMapIdMap = new ConcurrentHashMap<>();

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

        if (data == null) {
            // the map would be unstorable anyway (no nulls in a concurrent map), and silently keeping
            // the player's previous entry is less broken than wiping it for nothing
            ExileLog.get().warn("Tried to store null map data for " + p.getScoreboardName() + ", ignoring.");
            return;
        }

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
