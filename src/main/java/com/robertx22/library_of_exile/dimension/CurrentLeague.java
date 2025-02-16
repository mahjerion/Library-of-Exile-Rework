package com.robertx22.library_of_exile.dimension;

import com.robertx22.library_of_exile.components.AllMapConnectionData;
import com.robertx22.library_of_exile.components.MapConnectionsCap;
import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

public class CurrentLeague {

    // your map dimension
    public MapDimensionInfo dimension;
    // if youre in a boss room for example
    public Optional<MapStructure> structure;
    // in case you are in side content and connected to primary content
    public Optional<AllMapConnectionData.Data> connectedFrom = Optional.empty();


    public CurrentLeague(MapDimensionInfo dimension, Optional<MapStructure> structure, Optional<AllMapConnectionData.Data> connectedFrom) {
        this.dimension = dimension;
        this.structure = structure;
        this.connectedFrom = connectedFrom;
    }

    // 5 options:
    // 1) you are in a map dimension, inside a structure and connected to another map
    // 2) you are in a map dimension, inside a structure, but not connected to anything
    // 3) you are in a map dimension, but not inside a structure, meaning you're probably bugged out
    // 5) you are in a map dimension, but not inside a structure, meaning you're probably bugged out but your map is connected
    // 6) you are not in a map dimension
    public static Optional<CurrentLeague> get(ServerLevel level, BlockPos pos) {
        var info = MapDimensions.getInfo(level);

        if (info != null) {
            if (info.isInside(info.structure, level, pos)) {
                return Optional.of(new CurrentLeague(info, Optional.of(info.structure), getConnectedMap(level, pos)));
            }
            var opt = info.secondaryStructures.stream().filter(x -> info.isInside(x, level, pos)).findAny();

            if (opt.isPresent()) {
                return Optional.of(new CurrentLeague(info, Optional.of(opt.get()), getConnectedMap(level, pos)));
            } else {
                return Optional.of(new CurrentLeague(info, Optional.empty(), getConnectedMap(level, pos)));
            }
        }
        return Optional.empty();
    }


    public boolean isAffectedBy(MapDimensionInfo info) {
        if (structure.isPresent() && info.hasStructure(structure.get())) {
            return true;
        }
        return info.hasStructure(dimension.structure);
    }

    public static Optional<AllMapConnectionData.Data> getConnectedMap(ServerLevel level, BlockPos pos) {
        AllMapConnectionData cons = MapConnectionsCap.get(level).data;
        var data = cons.getOriginalMap(level, pos);
        return data == null ? Optional.empty() : Optional.of(data);
    }

}
