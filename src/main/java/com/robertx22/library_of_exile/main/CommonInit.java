package com.robertx22.library_of_exile.main;

import com.robertx22.library_of_exile.components.OnMobDamaged;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.registers.client.S2CPacketRegister;
import com.robertx22.library_of_exile.registers.common.C2SPacketRegister;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryEvent;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.SyncTime;
import com.robertx22.library_of_exile.registry.util.ExileRegistryUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;

@Mod(Ref.MODID)
public class CommonInit {

    public static boolean RUN_DEV_TOOLS = false;

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ref.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ref.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ref.MODID);


    public static void initDeferred() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        CREATIVE_TAB.register(bus);
        BLOCKS.register(bus);
    }

    public static void registerEntries() {


    }


    public CommonInit() {

        if (RUN_DEV_TOOLS) {
            ExileRegistryUtil.setCurrentRegistarMod(Ref.MODID);

            ApiForgeEvents.registerForgeEvent(PlayerEvent.PlayerLoggedInEvent.class, event -> {
                new LibDataGen().run(CachedOutput.NO_CACHE);
                event.getEntity().sendSystemMessage(Component.literal("WARNING: Dev tools ON!"));
            });
        }

        // todo make this separate per each mod?   ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, MapDimensionConfig.SPEC, defaultConfigName(ModConfig.Type.SERVER, "exile_map_dimensions"));

        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();


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

        new LibModConstructor(Ref.MODID, bus);


    }

    public static String defaultConfigName(ModConfig.Type type, String modId) {
        // config file name would be "forge-client.toml" and "forge-server.toml"
        return String.format(Locale.ROOT, "%s-%s.toml", modId, type.extension());
    }

    public void interMod(InterModProcessEvent event) {

    }

    public void commonSetupEvent(FMLCommonSetupEvent event) {

        for (ExileRegistryType type : ExileRegistryType.getAllInRegisterOrder()) {
            var e = new ExileRegistryEvent(type);
            ExileEvents.EXILE_REGISTRY_GATHER.callEvents(e);
        }

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
