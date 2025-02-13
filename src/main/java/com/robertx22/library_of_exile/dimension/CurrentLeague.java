package com.robertx22.library_of_exile.dimension;

import com.robertx22.library_of_exile.components.AllMapConnectionData;
import com.robertx22.library_of_exile.components.MapConnectionsCap;
import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

public class CurrentLeague {

    public MapDimensionInfo map;
    // if youre in a boss room for example
    public MapStructure structureCurrentlyIn;
    // if youre in secondary content inside a dim, you will be affected by the primary content, if in a boss room, youre affected by the adventure map
    public MapStructure structureAffectedBy;

    public static Optional<CurrentLeague> get(ServerLevel level, BlockPos pos) {
        var info = MapDimensions.getInfo(level);

        if (info != null) {
            if (info.isInside(info.structure, level, pos)) {
                return Optional.of(new CurrentLeague(info, info.structure, info.structure));
            }
            var opt = info.secondaryStructures.stream().filter(x -> info.isInside(x, level, pos)).findAny();

            if (opt.isPresent()) {
                return Optional.of(new CurrentLeague(info, opt.get(), info.structure));
            }
        }
        return Optional.empty();
    }


    private CurrentLeague(MapDimensionInfo map, MapStructure structureCurrentlyIn, MapStructure structureAffectedBy) {
        this.map = map;
        this.structureCurrentlyIn = structureCurrentlyIn;
        this.structureAffectedBy = structureAffectedBy;
    }

    public boolean isAffectedBy(MapDimensionInfo info) {
        return info.hasStructure(structureAffectedBy) || info.hasStructure(structureAffectedBy);
    }

    public Optional<AllMapConnectionData.Data> getConnectedMap(ServerLevel level, BlockPos pos) {
        AllMapConnectionData cons = MapConnectionsCap.get(level).data;
        var data = cons.getOriginalMap(level, pos);
        return Optional.ofNullable(data);
    }

}
