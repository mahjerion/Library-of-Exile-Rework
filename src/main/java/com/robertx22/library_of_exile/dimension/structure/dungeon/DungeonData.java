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

    public String folder = "";

    public ExileTagRequirement<MobList> mob_list_tag_check = new ExileTagRequirement().createBuilder().includes(MobListTags.MAP).build();

    public List<String> entrances = new ArrayList<>();
    public List<String> four_ways = new ArrayList<>();
    public List<String> straight_hallways = new ArrayList<>();
    public List<String> curved_hallways = new ArrayList<>();
    public List<String> triple_hallway = new ArrayList<>();
    public List<String> ends = new ArrayList<>();

    private transient List<DungeonRoom> rooms = new ArrayList<>();


    public boolean checkValidity(ExileRegistry reg) {
        for (RoomType type : RoomType.values()) {
            if (getRoomList(type).isEmpty()) {
                ExileLog.get().warn("Dungeons must have rooms of each type! " + reg.getRegistryIdPlusGuid() + " is missing " + type.name() + " rooms");
                return false;
            }
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
