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

    // game time an instance key was last known to have a player standing in it.
    //
    // occupancy can only ever be sampled as "who is in the dimension at this instant", and the sweep
    // runs every time anyone starts a map - so a party that is briefly all dead, respawning, mid
    // teleport or relogging looked exactly like an abandoned instance and had its run deleted
    // underneath it. Losing the data is what makes the rest of an instance generate as some other,
    // randomly rolled map. A key now has to be unoccupied for a whole keepalive window before it goes.
    //
    // deliberately not serialized: game time survives a restart but "who was standing where" does not,
    // and a key with no stamp is treated as seen just now. That only ever delays a sweep, never causes
    // one, which is the safe direction for a store whose loss corrupts a run.
    private transient ConcurrentHashMap<String, Long> lastOccupied = new ConcurrentHashMap<>();

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

        map.put(key, data);
        playerMapIdMap.put(p.getStringUUID(), key);

        // this used to drop the player's previous instance unconditionally, which meant the store held at
        // most ONE live instance per player. a party runs the leader's map, only the leader owns the key,
        // and the moment the leader starts their next map the instance everyone else is still standing in
        // is deleted - the rest of the run then generates as a different, randomly rolled dungeon. only
        // let go of an instance once nobody has been inside it for a while.
        // if the dimension can't be resolved we can't tell who is where, so nothing is dropped at all.
        // keeping a dead instance costs a map entry, dropping a live one corrupts someone's run.
        //
        // the player's previous key gets no special case any more. Dropping it here the instant it looked
        // empty was the same bug in miniature, just scoped to one player: it skipped the keepalive window
        // entirely. Once playerMapIdMap has been repointed above, that key is simply an unowned key like
        // any other, and the sweep releases it on the same terms - unless somebody else owns it or is
        // standing in it.
        var mapLevel = getMapLevel(p, structure);
        if (mapLevel != null) {
            Set<String> occupied = occupiedKeys(mapLevel, structure);
            long now = p.level().getGameTime();

            // stamp before sweeping, so this player's brand new instance and every currently occupied one
            // start their clock now rather than being judged on a stamp they never had
            lastOccupied.put(key, now);
            for (String occupiedKey : occupied) {
                lastOccupied.put(occupiedKey, now);
            }

            // instances abandoned by players who logged out are never reclaimed by any other path, so
            // collect them here. map starts are rare, and this is one pass over the online players plus
            // one over the keys.
            sweepUnusedInstances(occupied, now, keepAliveTicks(structure));
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
        lastOccupied.clear();
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

    // what we fall back to when the config can't be read at all. NOT 0: a server owner setting 0 is
    // asking for the old release-on-sight behaviour, but an unrelated failure to read the value must not
    // quietly opt them into it. Keeping an instance too long costs one map entry; releasing one too
    // early corrupts somebody's run, so failures err towards keeping.
    private static final long DEFAULT_KEEPALIVE_TICKS = 30L * 60L * 20L;

    // how long an unoccupied instance of this structure's dimension is kept, in ticks. 0 restores the
    // old behaviour of releasing it as soon as it looks empty.
    private long keepAliveTicks(MapStructure structure) {
        try {
            var info = MapDimensions.getInfo(structure);
            if (info == null || info.config == null) {
                return DEFAULT_KEEPALIVE_TICKS;
            }
            return info.config.INSTANCE_KEEPALIVE_MINUTES.get() * 60L * 20L;
        } catch (Exception e) {
            e.printStackTrace();
            return DEFAULT_KEEPALIVE_TICKS;
        }
    }

    // drop every instance that no player is inside, no player still owns, and that has been that way for
    // longer than the keepalive window. an owner keeps their instance while offline, so logging out mid
    // map doesn't lose it.
    private void sweepUnusedInstances(Set<String> occupied, long now, long keepAliveTicks) {
        Set<String> owned = new HashSet<>(playerMapIdMap.values());

        map.keySet().removeIf(k -> {
            if (occupied.contains(k) || owned.contains(k)) {
                return false;
            }
            // first time this key has been seen unoccupied - start its clock instead of dropping it.
            // this is also what covers keys restored from disk, whose stamps aren't saved.
            Long since = lastOccupied.putIfAbsent(k, now);
            if (since == null) {
                return false;
            }
            // a stamp in the future means game time went backwards (restored backup, /time set). restart
            // the clock rather than keeping the instance alive forever.
            if (since > now) {
                lastOccupied.put(k, now);
                return false;
            }
            return now - since >= keepAliveTicks;
        });
        // an owner whose instance is gone owns nothing, and leaving the entry would make their next
        // setData try to remove a key that could since have been handed to someone else
        playerMapIdMap.entrySet().removeIf(e -> !map.containsKey(e.getValue()));
        lastOccupied.keySet().removeIf(k -> !map.containsKey(k));
    }
}
