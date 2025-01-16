package com.robertx22.library_of_exile.main;

import com.robertx22.library_of_exile.components.EntityInfoComponent;
import com.robertx22.library_of_exile.components.PlayerCapabilities;
import com.robertx22.library_of_exile.components.PlayerDataCapability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import java.util.function.Consumer;

public class Capabilities {

    public static void reg() {


        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, (Consumer<AttachCapabilitiesEvent<Entity>>) x -> {
            if (x.getObject() instanceof LivingEntity en) {
                x.addCapability(EntityInfoComponent.RESOURCE, new EntityInfoComponent.DefaultImpl(en));
            }
        });
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, (Consumer<AttachCapabilitiesEvent<Entity>>) x -> {
            if (x.getObject() instanceof Player en) {
                x.addCapability(PlayerDataCapability.RESOURCE, new PlayerDataCapability(en));
            }
        });


        ApiForgeEvents.registerForgeEvent(RegisterCapabilitiesEvent.class, x -> {
            x.register(EntityInfoComponent.DefaultImpl.class);
        });


        PlayerCapabilities.register(PlayerDataCapability.INSTANCE, new PlayerDataCapability(null));

        /*
        CapabilityManager.INSTANCE.register(
                EntityInfoComponent.IEntityInfo.class,
                new EntityInfoComponent.Storage(),
                () -> new EntityInfoComponent.DefaultImpl(null)
        );


         */
    }

}
