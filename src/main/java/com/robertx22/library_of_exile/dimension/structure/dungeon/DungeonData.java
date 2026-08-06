package com.robertx22.library_of_exile.dimension.structure.dungeon;

import com.robertx22.library_of_exile.database.mob_list.MobList;
import com.robertx22.library_of_exile.database.mob_list.MobListTags;
import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.tags.ExileTagRequirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class DungeonData {

    // a room is always a square of whole chunks. the grid of rooms is a fixed amount of cells, so bigger
    // rooms mean a bigger dungeon in blocks, not fewer rooms. changing this requires the instance spacing
    // to have room for GRID_CELLS * MAX_ROOM_CHUNKS chunks, which is why it's capped.
    public static final int MAX_ROOM_CHUNKS = 4;

    public String folder = "";

    // blocks per side of every room in this dungeon. must be a multiple of 16, all rooms must match it.
    public int room_size = 16;

    // 0 means fall back to whatever the host mod's config says
    public int min_rooms = 0;
    public int max_rooms = 0;

    public ExileTagRequirement<MobList> mob_list_tag_check = new ExileTagRequirement().createBuilder().includes(MobListTags.MAP).build();

    public List<String> entrances = new ArrayList<>();
    public List<String> four_ways = new ArrayList<>();
    public List<String> straight_hallways = new ArrayList<>();
    public List<String> curved_hallways = new ArrayList<>();
    public List<String> triple_hallway = new ArrayList<>();
    public List<String> ends = new ArrayList<>();

    // built once from the id lists above, then published as an immutable snapshot.
    //
    // worldgen resolves rooms from several threads at once, and the old "if (rooms.isEmpty())" lazy
    // init let two of them populate the same list simultaneously - which leaves every room in it
    // twice. Doubling the list changes which entry weightedRandom returns for the same roll, so a
    // layout rebuilt after a cache eviction resolved different room files than the layout it
    // replaced. That is exactly the determinism DungeonStructure.builtDungeonCache's eviction is
    // documented to rely on.
    //
    // roomsByType is written before rooms, so any thread that sees a non null rooms is guaranteed to
    // see roomsByType too.
    private transient volatile List<DungeonRoom> rooms = null;
    private transient volatile Map<RoomType, List<DungeonRoom>> roomsByType = null;


    // how many chunks wide/long a single room of this dungeon is
    public int getRoomSizeInChunks() {
        return Math.max(1, room_size / 16);
    }

    public boolean checkValidity(ExileRegistry reg) {
        for (RoomType type : RoomType.values()) {
            if (getRoomList(type).isEmpty()) {
                ExileLog.get().warn("Dungeons must have rooms of each type! " + reg.getRegistryIdPlusGuid() + " is missing " + type.name() + " rooms");
                return false;
            }
        }
        if (room_size < 16 || room_size % 16 != 0) {
            ExileLog.get().warn("Dungeon room_size must be a multiple of 16! " + reg.getRegistryIdPlusGuid() + " has " + room_size);
            return false;
        }
        if (getRoomSizeInChunks() > MAX_ROOM_CHUNKS) {
            ExileLog.get().warn("Dungeon room_size can't be bigger than " + (MAX_ROOM_CHUNKS * 16) + "! " + reg.getRegistryIdPlusGuid() + " has " + room_size);
            return false;
        }
        return true;
    }

    public List<DungeonRoom> getRooms() {
        List<DungeonRoom> built = rooms;
        if (built != null) {
            return built;
        }
        synchronized (this) {
            if (rooms == null) {
                List<DungeonRoom> all = new ArrayList<>();
                addRooms(all, RoomType.ENTRANCE, entrances);
                addRooms(all, RoomType.FOUR_WAY, four_ways);
                addRooms(all, RoomType.STRAIGHT_HALLWAY, straight_hallways);
                addRooms(all, RoomType.CURVED_HALLWAY, curved_hallways);
                addRooms(all, RoomType.TRIPLE_HALLWAY, triple_hallway);
                addRooms(all, RoomType.END, ends);

                Map<RoomType, List<DungeonRoom>> byType = new EnumMap<>(RoomType.class);
                for (RoomType type : RoomType.values()) {
                    List<DungeonRoom> ofType = new ArrayList<>();
                    for (DungeonRoom room : all) {
                        if (room.type.equals(type)) {
                            ofType.add(room);
                        }
                    }
                    byType.put(type, Collections.unmodifiableList(ofType));
                }

                roomsByType = byType;
                rooms = Collections.unmodifiableList(all);
            }
        }
        return rooms;
    }

    // pre split per type instead of filtering the full list on every call. this runs once per room
    // placed, for every room of every layout build, so the stream and its copy were pure overhead.
    public List<DungeonRoom> getRoomsOfType(RoomType type) {
        getRooms();
        return roomsByType.getOrDefault(type, Collections.emptyList());
    }

    public List<String> getRoomList(RoomType type) {

        if (type == RoomType.END) {
            return ends;
        }
        if (type == RoomType.ENTRANCE) {
            return entrances;
        }
        if (type == RoomType.FOUR_WAY) {
            return four_ways;
        }
        if (type == RoomType.CURVED_HALLWAY) {
            return curved_hallways;
        }
        if (type == RoomType.STRAIGHT_HALLWAY) {
            return straight_hallways;
        }
        if (type == RoomType.TRIPLE_HALLWAY) {
            return triple_hallway;
        }

        return Arrays.asList();
    }


    private void addRooms(List<DungeonRoom> into, RoomType type, List<String> list) {
        for (String room : list) {
            into.add(new DungeonRoom(this.folder, room, type));
        }
    }

    public final boolean hasRoomFor(RoomType type) {
        return !getRoomsOfType(type).isEmpty();
    }
}
