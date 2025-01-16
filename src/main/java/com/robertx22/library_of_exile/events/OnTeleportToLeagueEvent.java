package com.robertx22.library_of_exile.events;

import com.robertx22.library_of_exile.events.base.ExileEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

// todo
// can use this event to connect a map to a league mechanic.
// if i'm teleporting from a map, add the pos of this tp to the list
// get map method would check if map has a [world, startpos]

public class OnTeleportToLeagueEvent extends ExileEvent {

    public ServerPlayer player;

    public BlockPos fromPos;
    public ServerLevel fromWorld;

    public BlockPos toPos;
    public ResourceLocation toWorld;

    public OnTeleportToLeagueEvent(ServerPlayer player, BlockPos fromPos, ServerLevel fromWorld, BlockPos toPos, ResourceLocation toWorld) {
        this.player = player;
        this.fromPos = fromPos;
        this.fromWorld = fromWorld;
        this.toPos = toPos;
        this.toWorld = toWorld;
    }
}
