package com.robertx22.library_of_exile.database.map_data_block.all;

import com.robertx22.library_of_exile.database.map_data_block.MapDataBlock;
import com.robertx22.library_of_exile.entries.InvisibleDataBlockEntity;
import com.robertx22.library_of_exile.main.ExileLibEntries;
import com.robertx22.library_of_exile.util.wiki.WikiEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class SetInvisDataBlockMB extends MapDataBlock {

    public String data_id = "";

    public SetInvisDataBlockMB(String id, String data_id) {
        super("set_invis_data_block", id);
        this.data_id = data_id;
    }

    @Override
    public Class<?> getClassForSerialization() {
        return SetBlockMB.class;
    }

    @Override
    public void processImplementationINTERNAL(String key, BlockPos pos, Level world, CompoundTag nbt) {
        world.setBlock(pos, ExileLibEntries.INVISIBLE_DATA_BLOCK.get().defaultBlockState(), 2);

        if (world.getBlockEntity(pos) instanceof InvisibleDataBlockEntity be) {
            be.type = data_id;
            be.setChanged();
        }
    }

    @Override
    public WikiEntry getWikiEntry() {
        return WikiEntry.none();
    }
}
