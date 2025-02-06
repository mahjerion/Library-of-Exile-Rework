package com.robertx22.library_of_exile.components;

import com.robertx22.library_of_exile.dimension.teleport.SavedTeleportPos;
import com.robertx22.library_of_exile.utils.CommandUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

public class DelayedTeleportData {
    public String command = "";
    // delaying the teleport might or might not help, don't know
    public int ticks = 0;
    public SavedTeleportPos tp = new SavedTeleportPos();

    public DelayedTeleportData(String command, int ticks, SavedTeleportPos tp) {
        this.command = command;
        this.ticks = ticks;
        this.tp = tp;
    }

    public void teleport(Player p) {

        try {

            // getting the chunk SHOULD prevent server freezes, DO NOT REMOVE!!!
            var key = ResourceKey.create(Registries.DIMENSION, tp.getDimensionId());
            var level = p.getServer().getLevel(key);
            level.getChunk(tp.getPos());
            CommandUtils.execute(p, command);
            this.command = "";

        } catch (Exception e) {
            e.printStackTrace();
            p.sendSystemMessage(Component.literal("Teleport failed, check log for error."));
        }

    }
}
