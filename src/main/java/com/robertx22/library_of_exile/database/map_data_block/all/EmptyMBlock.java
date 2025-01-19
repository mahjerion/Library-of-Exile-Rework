package com.robertx22.library_of_exile.database.map_data_block.all;

import com.robertx22.library_of_exile.database.map_data_block.MapDataBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class EmptyMBlock extends MapDataBlock {
    public EmptyMBlock(String id) {
        super("empty", id);
    }

    @Override
    public Class<?> getClassForSerialization() {
        return EmptyMBlock.class;
    }

    @Override
    public void processImplementationINTERNAL(String key, BlockPos pos, Level world, CompoundTag nbt) {

    }
}
