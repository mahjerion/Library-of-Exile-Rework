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
        public IDungeon dungeon;

        /**
         * @deprecated Use {@link #Settings(Random, int, int, IDungeon)} instead.
         */
        @Deprecated
        public Settings(Random ran, int minRooms, int maxRooms, List<IDungeon> dungeons) {
            this.ran = ran;
            this.minRooms = minRooms;
            this.maxRooms = maxRooms;
            this.dungeon = RandomUtils.weightedRandom(dungeons, ran.nextDouble());
        }

        public Settings(Random ran, int minRooms, int maxRooms, IDungeon dungeon) {
            this.ran = ran;
            this.minRooms = minRooms;
            this.maxRooms = maxRooms;
            this.dungeon = dungeon;
        }
    }


    // random must be deterministic, 1 dungeon = 1 random
    public DungeonBuilder(Settings settings) {
        this.rand = settings.ran;
        this.dungeon = settings.dungeon;
        this.size = RandomUtils.RandomRange(settings.minRooms, settings.maxRooms, rand);
    }


    public IDungeon dungeon;
    public BuiltDungeon builtDungeon;
    public final Random rand;
    public int size;


    // how many chunks wide a single room of this dungeon is. 1 for every pre existing dungeon.
    public int getRoomChunks() {
        return dungeon == null ? 1 : dungeon.getDungeonData().getRoomSizeInChunks();
    }


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