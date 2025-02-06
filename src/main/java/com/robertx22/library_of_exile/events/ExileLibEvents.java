package com.robertx22.library_of_exile.events;

import com.robertx22.library_of_exile.components.PlayerDataCapability;
import com.robertx22.library_of_exile.main.ApiForgeEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

public class ExileLibEvents {

    public static void init() {

        ApiForgeEvents.registerForgeEvent(TickEvent.PlayerTickEvent.class, event ->
        {
            Player p = event.player;

            if (p.level().isClientSide || event.phase != TickEvent.Phase.END) {
                return;
            }
            var delayed = PlayerDataCapability.get(p).delayedTeleportData;
            if (delayed != null) {
                if (!delayed.command.isEmpty()) {
                    if (delayed.ticks-- < 1) {
                        delayed.teleport(p);
                    }
                }
            }
        });
    }
}
