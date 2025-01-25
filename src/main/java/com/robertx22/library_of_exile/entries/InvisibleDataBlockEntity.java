package com.robertx22.library_of_exile.entries;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.database.invis_block.InvisibleData;
import com.robertx22.library_of_exile.main.ExileLibEntries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Optional;

public class InvisibleDataBlockEntity extends BlockEntity {

    public String type = "";

    public int ticks = 0;
    // for saving custom data if really needed..?
    public HashMap<String, Object> map = new HashMap<>();

    public InvisibleDataBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ExileLibEntries.INVISIBLE_DATA_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public Optional<InvisibleData> get() {
        return LibDatabase.InvisibleData().getOptional(type);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putString("type", type);
        get().ifPresent(x -> x.saveAdditional(this, nbt));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.type = pTag.getString("type");
        get().ifPresent(x -> x.load(this, pTag));
    }
}
