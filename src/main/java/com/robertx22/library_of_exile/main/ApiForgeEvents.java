package com.robertx22.library_of_exile.main;

import com.robertx22.library_of_exile.components.EntityInfoComponent;
import com.robertx22.library_of_exile.components.PlayerCapabilities;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;

public class ApiForgeEvents {

    public static <T extends Event> void registerForgeEvent(Class<T> clazz, Consumer<T> event, EventPriority priority) {
        if (IModBusEvent.class.isAssignableFrom(clazz) || clazz.isAssignableFrom(IModBusEvent.class)) {
            FMLJavaModLoadingContext.get()
                    .getModEventBus()
                    .addListener(priority, event);
        } else {
            MinecraftForge.EVENT_BUS.addListener(priority, event);
        }
    }

    public static <T extends Event> void registerForgeEvent(Class<T> clazz, Consumer<T> event) {
        registerForgeEvent(clazz, event, EventPriority.NORMAL);
    }

    public static void register() {

        registerForgeEvent(LivingAttackEvent.class, event -> {
            ExileEvents.OnDamageEntity after = ExileEvents.DAMAGE_BEFORE_CALC.callEvents(
                    new ExileEvents.OnDamageEntity(event.getSource(), event.getAmount(), event.getEntity())
            );
            if (after.canceled) {
                event.setCanceled(true);
            }
            // todo is needed? event.setAmount(after.damage);
        }, EventPriority.HIGHEST);

        registerForgeEvent(LivingDamageEvent.class, event -> {
            ExileEvents.OnDamageEntity after = ExileEvents.DAMAGE_AFTER_CALC.callEvents(
                    new ExileEvents.OnDamageEntity(event.getSource(), event.getAmount(), event.getEntity())
            );
            event.setAmount(after.damage);
        }, EventPriority.LOWEST);

        registerForgeEvent(LivingEvent.LivingTickEvent.class, event -> {
            LivingEntity entity = event.getEntity();

            if (entity.tickCount == 0) {
                EntityInfoComponent.get(entity).spawnInit(entity);// todo
            }

            ExileEvents.LIVING_ENTITY_TICK.callEvents(new ExileEvents.OnEntityTick(entity));
        });

        /*
        registerForgeEvent(AddReloadListenerEvent.class, event -> {
            ExileRegistryType.registerJsonListeners(event);
        });

         */

        registerForgeEvent(LivingDeathEvent.class, event -> {
            if (event.getEntity() instanceof Player == false && event.getSource()
                    .getEntity() instanceof LivingEntity) {
                ExileEvents.MOB_DEATH.callEvents(new ExileEvents.OnMobDeath(event.getEntity(), (LivingEntity) event.getSource()
                        .getEntity()));
            }
        });

        registerForgeEvent(PlayerEvent.Clone.class, event -> {
            PlayerCapabilities.saveAllOnDeath(event);
            PlayerCapabilities.syncAllToClient(event.getEntity());
        });

        registerForgeEvent(ServerStartedEvent.class, event -> {
            CommonInit.onDatapacksReloaded();
        });

        registerForgeEvent(AddReloadListenerEvent.class, event -> {
            CommonInit.onDatapacksReloaded();
        });
    }
}