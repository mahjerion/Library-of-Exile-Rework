package com.robertx22.library_of_exile.database.invis_block;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.entries.InvisibleDataBlockEntity;
import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public abstract class InvisibleData implements ExileRegistry<InvisibleData> {

    public abstract void saveAdditional(InvisibleDataBlockEntity be, CompoundTag nbt);

    public abstract void load(InvisibleDataBlockEntity be, CompoundTag pTag);

    public abstract void tick(InvisibleDataBlockEntity be, Level world, BlockPos pos);

    @Override
    public ExileRegistryType getExileRegistryType() {
        return LibDatabase.INVISIBLE_DATA;
    }

}
