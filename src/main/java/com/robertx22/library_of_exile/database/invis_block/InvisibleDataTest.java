package com.robertx22.library_of_exile.database.invis_block;

import com.robertx22.library_of_exile.entries.InvisibleDataBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class InvisibleDataTest extends InvisibleData {

    @Override
    public void saveAdditional(InvisibleDataBlockEntity be, CompoundTag nbt) {

    }

    @Override
    public void load(InvisibleDataBlockEntity be, CompoundTag pTag) {

    }

    @Override
    public void tick(InvisibleDataBlockEntity be, Level world, BlockPos pos) {

    }

    @Override
    public String GUID() {
        return "test";
    }

    @Override
    public int Weight() {
        return 0;
    }
}
