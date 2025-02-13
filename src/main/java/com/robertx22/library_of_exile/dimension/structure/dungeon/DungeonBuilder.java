package com.robertx22.library_of_exile.dimension.structure.dungeon;


import com.robertx22.library_of_exile.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonBuilder {

    public static class Settings {

        public Random ran;
        public int minRooms;
        public int maxRooms;
        public List<IDungeon> possibleDungeons;

        public Settings(Random ran, int minRooms, int maxRooms, List<IDungeon> possibleDungeons) {
            this.ran = ran;
            this.minRooms = minRooms;
            this.maxRooms = maxRooms;
            this.possibleDungeons = possibleDungeons;
        }
    }


    // random must be deterministic, 1 dungeon = 1 random
    public DungeonBuilder(Settings settings) {
        this.rand = settings.ran;
        this.dungeon = RandomUtils.weightedRandom(settings.possibleDungeons, rand.nextDouble());
        this.size = RandomUtils.RandomRange(settings.minRooms, settings.maxRooms, rand);
    }


    public IDungeon dungeon;
    public BuiltDungeon builtDungeon;
    public final Random rand;
    public int size;


    public void build() {
        builtDungeon = new BuiltDungeon(size, this);
        builtDungeon.setupBarriers();
        setupEntrance();
        builtDungeon.fillWithBarriers();
    }


    public RoomRotation random(List<RoomRotation> list) {
        return RandomUtils.weightedRandom(list, rand.nextDouble());
    }

    private void setupEntrance() {
        DungeonRoom entranceRoom = RoomType.ENTRANCE.getRandomRoom(dungeon, this);
        List<RoomRotation> possible = new ArrayList<>();
        possible.addAll(RoomType.ENTRANCE.getRotations());
        RoomRotation rotation = random(possible);
        BuiltRoom entrance = new BuiltRoom(rotation, entranceRoom);
        int mid = builtDungeon.getMiddle();
        builtDungeon.addRoom(mid, mid, entrance);
    }

}