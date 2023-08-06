package com.robertx22.library_of_exile.events.base;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public class EnsureTeleportData {


    ServerPlayer player;
    Consumer<EnsureTeleportData> action;
    int ticksLeft;
    int ticks = 0;
    BlockPos whereShouldTeleport;
    ResourceLocation dimensionToTpTo;
    int tries = 0;

    int origTicksLeft;

    public EnsureTeleportData(ServerPlayer player, Consumer<EnsureTeleportData> action, int ticksLeft, BlockPos whereShouldTeleport, ResourceLocation dimensionToTpTo) {
        this.player = player;
        this.action = action;
        this.ticksLeft = ticksLeft;
        this.whereShouldTeleport = whereShouldTeleport;
        this.dimensionToTpTo = dimensionToTpTo;
        this.origTicksLeft = ticksLeft;
    }

    public void cancel() {
        this.ticksLeft = -1;
    }

    public void resetTicks() {
        this.ticks = 0;
        this.ticksLeft = origTicksLeft;
    }
}