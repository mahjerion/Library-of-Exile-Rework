package com.robertx22.library_of_exile.main;

import com.robertx22.library_of_exile.components.OnMobDamaged;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.registers.client.S2CPacketRegister;
import com.robertx22.library_of_exile.registers.common.C2SPacketRegister;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.SyncTime;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Ref.MODID)
public class CommonInit {

    public CommonInit() {

        final IEventBus bus = FMLJavaModLoadingContext.get()
                .getModEventBus();

        bus.addListener(this::commonSetupEvent);
        bus.addListener(this::interMod);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::clientSetup);
        });

        ApiForgeEvents.register();


        ApiForgeEvents.registerForgeEvent(OnDatapackSyncEvent.class, x -> {
            ServerPlayer player = x.getPlayer();
            Database.sendPacketsToClient(player, SyncTime.ON_LOGIN);
        });


        C2SPacketRegister.register();
        S2CPacketRegister.register();

        ExileEvents.DAMAGE_AFTER_CALC.register(new OnMobDamaged());


        System.out.println("Library of Exile loaded.");
    }

    public void interMod(InterModProcessEvent event) {

    }

    public void commonSetupEvent(FMLCommonSetupEvent event) {
        Capabilities.reg();
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        ClientInit.onInitializeClient();
    }

    public static void onDatapacksReloaded() {
        try {

            //  Database.backup();
            Database.checkGuidValidity();
            Database.unregisterInvalidEntries();
            Database.getAllRegistries()
                    .forEach(x -> x.onAllDatapacksLoaded());
            ExileEvents.AFTER_DATABASE_LOADED.callEvents(new ExileEvents.AfterDatabaseLoaded());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
