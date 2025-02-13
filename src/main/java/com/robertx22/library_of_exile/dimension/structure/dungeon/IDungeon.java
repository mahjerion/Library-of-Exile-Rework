package com.robertx22.library_of_exile.dimension.structure.dungeon;

import com.robertx22.library_of_exile.registry.IGUID;
import com.robertx22.library_of_exile.registry.IWeighted;

public interface IDungeon extends IWeighted, IGUID {

    public DungeonData getDungeonData();
}
