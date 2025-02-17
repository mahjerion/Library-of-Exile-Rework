package com.robertx22.library_of_exile.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Optional;

public abstract class MapDataFinder<T> {

    public abstract T getData(Pos pos);

    public abstract MapDimensionInfo getInfo();

    public static class Pos {
        public Level level;
        public BlockPos pos;


        public Pos(Level level, BlockPos pos) {
            this.level = level;
            this.pos = pos;
        }
    }

    public Optional<T> ifMapData(Level level, BlockPos pos) {
        return ifMapData(level, pos, true);
    }

    public Optional<T> ifMapData(Level level, BlockPos pos, boolean grabConnectedData) {
        if (level.isClientSide) {
            return Optional.empty();
        }
        var map = MapDimensions.getInfo(level);
        if (map != null && map.dimensionId.equals(getInfo().dimensionId)) {
            var mapdata = getData(new Pos(level, pos));
            if (mapdata != null) {
                return Optional.of(mapdata);
            }
        }
        if (grabConnectedData) {
            if (map != null) {
                if (map.contentType == MapContentType.SIDE_CONTENT) {
                    var con = CurrentLeague.getConnectedMap((ServerLevel) level, pos);
                    if (con.isPresent()) {
                        if (con.get().struc.dimensionId.equals(getInfo().dimensionId)) {
                            var mapdata = getData(new Pos(level, con.get().cp.getMiddleBlockPosition(5)));
                            if (mapdata != null) {
                                return Optional.of(mapdata);
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }
}
