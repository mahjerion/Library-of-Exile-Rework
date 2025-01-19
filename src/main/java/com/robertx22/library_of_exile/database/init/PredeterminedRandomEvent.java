package com.robertx22.library_of_exile.database.init;

import com.robertx22.library_of_exile.events.base.ExileEvent;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class PredeterminedRandomEvent extends ExileEvent {

    public ExileRegistryType registryType;
    public Level level;
    public BlockPos pos;

    public String result = "";

    public PredeterminedRandomEvent(ExileRegistryType registryType, Level level, BlockPos pos) {
        this.registryType = registryType;
        this.level = level;
        this.pos = pos;
    }
}
