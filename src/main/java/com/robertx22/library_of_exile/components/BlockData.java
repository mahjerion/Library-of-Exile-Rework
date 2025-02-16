package com.robertx22.library_of_exile.components;

import net.minecraft.core.BlockPos;

public class BlockData {

    public Long pos = 0L;
    public String data = "";

    public BlockData(Long pos, String data) {
        this.pos = pos;
        this.data = data;
    }

    public BlockPos getPos() {
        return BlockPos.of(pos);
    }
}
