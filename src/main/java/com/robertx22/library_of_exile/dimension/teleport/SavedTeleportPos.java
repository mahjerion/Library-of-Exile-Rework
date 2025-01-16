package com.robertx22.library_of_exile.dimension.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SavedTeleportPos {

    public long pos = 0;
    public String dim = "";

    public ResourceLocation getDimensionId() {
        return new ResourceLocation(dim);
    }

    public BlockPos getPos() {
        return BlockPos.of(pos);

    }

    public void setFrom(Player p) {
        this.pos = p.blockPosition().asLong();
        this.dim = p.level().dimensionTypeId().location().toString();
    }

    public boolean isEmpty() {
        return pos == 0 && dim.isEmpty();
    }
}
