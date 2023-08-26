package com.robertx22.library_of_exile.components;

import com.robertx22.library_of_exile.events.base.EventConsumer;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class OnMobDamaged extends EventConsumer<ExileEvents.OnDamageEntity> {

    @Override
    public void accept(ExileEvents.OnDamageEntity event) {

        Entity attacker = event.source.getEntity();

        EntityInfoComponent.IEntityInfo comp = EntityInfoComponent.get(event.mob);
        if (comp != null) {
            if (event.mob instanceof Player == false) {
                comp.getDamageStats().onDamagedBy(attacker, event.damage);
            }
        }
    }

    // call after my main mod changes damage
    @Override
    public int callOrder() {
        return 10;
    }
}
