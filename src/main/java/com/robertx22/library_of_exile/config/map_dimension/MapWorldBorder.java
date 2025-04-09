package com.robertx22.library_of_exile.config.map_dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.border.WorldBorder;

public class MapWorldBorder extends WorldBorder {

    private static MapWorldBorder instance = null;

    public static MapWorldBorder get(Settings set) {
        if (instance == null) {
            instance = new MapWorldBorder(set);
        }
        return instance;
    }

    public MapWorldBorder(Settings settings) {
        this.applySettings(settings);
    }

    @Override
    public double getDistanceToBorder(Entity pEntity) {
        return 1000000; // todo or infinite?
    }

    @Override
    public boolean isWithinBounds(BlockPos pPos) {
        return true;
    }

    @Override
    public boolean isWithinBounds(ChunkPos pChunkPos) {
        return true;
    }

    @Override
    public boolean isWithinBounds(double pX, double pZ) {
        return true;
    }

    @Override
    public boolean isWithinBounds(double pX, double pZ, double pOffset) {
        return true;
    }

    @Override
    public double getDamagePerBlock() {
        return 0;
    }
}
