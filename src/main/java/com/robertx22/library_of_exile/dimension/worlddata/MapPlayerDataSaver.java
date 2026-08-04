package com.robertx22.library_of_exile.dimension.worlddata;

import com.robertx22.library_of_exile.components.AllMapConnectionData;
import com.robertx22.library_of_exile.components.MapConnectionsCap;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
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

        var start = structure.getStartChunkPos(new ChunkPos(pos));
        String key = getKey(start);

        String oldKey = playerMapIdMap.get(p.getStringUUID());

        map.put(key, data);
        playerMapIdMap.put(p.getStringUUID(), key);

        // this used to drop the player's previous instance unconditionally, which meant the store held at
        // most ONE live instance per player. a party runs the leader's map, only the leader owns the key,
        // and the moment the leader starts their next map the instance everyone else is still standing in
        // is deleted - the rest of the run then generates as a different, randomly rolled dungeon. only
        // let go of an instance once nobody is inside it.
        // if the dimension can't be resolved we can't tell who is where, so nothing is dropped at all.
        // keeping a dead instance costs a map entry, dropping a live one corrupts someone's run.
        var mapLevel = getMapLevel(p, structure);
        if (mapLevel != null) {
            Set<String> occupied = occupiedKeys(mapLevel, structure);

            if (oldKey != null && !oldKey.equals(key) && !occupied.contains(oldKey)) {
                map.remove(oldKey);
            }
            // the previous instance may have survived above while still occupied, and instances abandoned
            // by players who logged out are never reclaimed by any other path, so collect them here. map
            // starts are rare, and this is one pass over the online players plus one over the keys.
            sweepUnusedInstances(occupied);
        }

        // map connections. this has to run even when the player is NOT standing in a map (origin null),
        // because that is precisely the case that has to clear a recycled tile's stale connection.
        AllMapConnectionData cons = MapConnectionsCap.get(p.level()).data;
        cons.updateConnection(MapDimensions.getInfo(p.level()), p.blockPosition(), MapDimensions.getInfo(structure), pos);
    }

    public String getKey(ChunkPos cp) {
        return cp.x + "_" + cp.z;
    }

    // for the dimension wipe. the uuid is regenerated too, so block entities pointing at instances from
    // the previous session (MapDeviceBE.isActivated) correctly report themselves as dead.
    public void clearAll() {
        map.clear();
        playerMapIdMap.clear();
        uuid = UUID.randomUUID().toString();
    }

    // the level the instances of this structure live in, or null if it can't be resolved. every caller
    // treats null as "don't touch anything", so a missing dimension can never cause data loss.
    @Nullable
    private ServerLevel getMapLevel(Player p, MapStructure structure) {
        try {
            var info = MapDimensions.getInfo(structure);
            if (info == null || p.getServer() == null) {
                return null;
            }
            return p.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, info.dimensionId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // the instance key of every online player currently in that dimension
    private Set<String> occupiedKeys(ServerLevel level, MapStructure structure) {
        Set<String> occupied = new HashSet<>();
        for (Player p : level.players()) {
            occupied.add(getKey(structure.getStartChunkPos(p.blockPosition())));
        }
        return occupied;
    }

    // drop every instance that no player is inside and no player still owns. an owner keeps their
    // instance while offline, so logging out mid map doesn't lose it.
    private void sweepUnusedInstances(Set<String> occupied) {
        Set<String> owned = new HashSet<>(playerMapIdMap.values());

        map.keySet().removeIf(k -> !occupied.contains(k) && !owned.contains(k));
        // an owner whose instance is gone owns nothing, and leaving the entry would make their next
        // setData try to remove a key that could since have been handed to someone else
        playerMapIdMap.entrySet().removeIf(e -> !map.containsKey(e.getValue()));
    }
}
