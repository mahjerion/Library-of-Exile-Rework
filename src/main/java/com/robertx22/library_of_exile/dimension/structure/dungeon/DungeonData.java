package com.robertx22.library_of_exile.dimension.structure.dungeon;

import com.robertx22.library_of_exile.database.mob_list.MobList;
import com.robertx22.library_of_exile.database.mob_list.MobListTags;
import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.tags.ExileTagRequirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private transient List<DungeonRoom> rooms = new ArrayList<>();


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
        if (rooms.isEmpty()) {
            addRooms(RoomType.ENTRANCE, entrances);
            addRooms(RoomType.FOUR_WAY, four_ways);
            addRooms(RoomType.STRAIGHT_HALLWAY, straight_hallways);
            addRooms(RoomType.CURVED_HALLWAY, curved_hallways);
            addRooms(RoomType.TRIPLE_HALLWAY, triple_hallway);
            addRooms(RoomType.END, ends);
        }
        return rooms;
    }

    public List<DungeonRoom> getRoomsOfType(RoomType type) {
        return getRooms()
                .stream()
                .filter(x -> x.type.equals(type)).collect(Collectors.toList());

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


    private void addRooms(RoomType type, List<String> list) {
        for (String room : list) {
            DungeonRoom b = new DungeonRoom(this.folder, room, type);
            this.rooms.add(b);
        }
    }

    public final boolean hasRoomFor(RoomType type) {
        return getRooms()
                .stream()
                .anyMatch(x -> x.type.equals(type));

    }
}
