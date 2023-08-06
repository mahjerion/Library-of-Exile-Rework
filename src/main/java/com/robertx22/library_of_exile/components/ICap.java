package com.robertx22.library_of_exile.components;

import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.library_of_exile.packets.SyncPlayerCapToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface ICap extends ICapabilitySerializable<CompoundTag> {
    public String getCapIdForSyncing();

    public default void syncToClient(Player player) {
        Packets.sendToClient(player, new SyncPlayerCapToClient(player, getCapIdForSyncing()));
    }
}
