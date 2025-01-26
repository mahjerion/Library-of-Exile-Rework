package com.robertx22.library_of_exile.database.map_data_block.all;

import com.robertx22.library_of_exile.database.map_data_block.MapDataBlock;
import com.robertx22.library_of_exile.vanilla_util.main.VanillaUTIL;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class SetBlockMB extends MapDataBlock {

    public String block_id = "";

    // todo i have to figure out if gson has problem instantiating classes without an empty constructor
    public SetBlockMB(String id, String block_id) {
        super("set_block", id);
        this.block_id = block_id;
    }

    @Override
    public Class<?> getClassForSerialization() {
        return SetBlockMB.class;
    }

    @Override
    public void processImplementationINTERNAL(String key, BlockPos pos, Level world, CompoundTag nbt) {
        var block = VanillaUTIL.REGISTRY.blocks().get(new ResourceLocation(block_id));
        world.setBlock(pos, block.defaultBlockState(), 2);
    }
}
