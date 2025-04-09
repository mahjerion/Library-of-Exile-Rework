package com.robertx22.library_of_exile.main;

import com.robertx22.library_of_exile.components.OnMobDamaged;
import com.robertx22.library_of_exile.database.affix.base.MobAffixEvents;
import com.robertx22.library_of_exile.events.ExileLibEvents;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.registers.client.S2CPacketRegister;
import com.robertx22.library_of_exile.registers.common.C2SPacketRegister;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryEvent;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.SyncTime;
import com.robertx22.library_of_exile.registry.helpers.OrderedModConstructor;
import com.robertx22.library_of_exile.registry.util.ExileRegistryUtil;
import com.robertx22.library_of_exile.unidentified.IdentifiableItems;
import com.robertx22.library_of_exile.util.wiki.WikiEntryCommands;
import com.robertx22.orbs_of_crafting.main.OrbsOfCraftingMain;
import com.robertx22.orbs_of_crafting.misc.OnClick;
import com.robertx22.orbs_of_crafting.register.ExileCurrency;
import com.robertx22.orbs_of_crafting.register.Orbs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Mod(Ref.MODID)
public class CommonInit {

    public static boolean RUN_DEV_TOOLS = false;

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ref.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ref.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Ref.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ref.MODID);


    public static void initDeferred() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        CREATIVE_TAB.register(bus);
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);

        CREATIVE_TAB.register("tab", () -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 2)
                .icon(() -> Orbs.INSTANCE.LEGENDARY_TOOL_ENCHANT.getItem().getDefaultInstance())
                .title(LibWords.MOD_NAME.get().withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD))
                .displayItems(new CreativeModeTab.DisplayItemsGenerator() {
                    @Override
                    public void accept(CreativeModeTab.ItemDisplayParameters param, CreativeModeTab.Output output) {
                        for (Item item : ExileCurrency.CACHED_MAP.get().keySet()) {
                            output.accept(item);
                        }
                    }
                })
                .build());
    }

    public static void registerEntries() {


    }


    public CommonInit() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        OrderedModConstructor.register(new LibModConstructor(Ref.MODID), bus);

        OrbsOfCraftingMain.init(bus);


        if (RUN_DEV_TOOLS) {
            ExileRegistryUtil.setCurrentRegistarMod(Ref.MODID);

            ApiForgeEvents.registerForgeEvent(PlayerEvent.PlayerLoggedInEvent.class, event -> {
                new LibDataGen().run(CachedOutput.NO_CACHE);
                event.getEntity().sendSystemMessage(Component.literal("WARNING: Dev tools ON!"));
            });
        }

        // todo make this separate per each mod?   ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, MapDimensionConfig.SPEC, defaultConfigName(ModConfig.Type.SERVER, "exile_map_dimensions"));


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
        MobAffixEvents.init();

        C2SPacketRegister.register();
        S2CPacketRegister.register();

        ExileLibEvents.init();
        IdentifiableItems.init();

        ExileEvents.DAMAGE_AFTER_CALC.register(new OnMobDamaged());


        ApiForgeEvents.registerForgeEvent(LivingDeathEvent.class, x -> {
            if (x.getEntity() instanceof ServerPlayer p) {
                ExileEvents.PLAYER_DEATH.callEvents(new ExileEvents.OnPlayerDeath(p));
            }
        }, EventPriority.LOWEST);

        // todo this should be here..
        ApiForgeEvents.registerForgeEvent(AddReloadListenerEvent.class, event -> {
            ExileRegistryType.registerJsonListeners(event);
        });

        ApiForgeEvents.registerForgeEvent(RegisterCommandsEvent.class, event -> {
            WikiEntryCommands.init(event.getDispatcher());
        });

        OnClick.register();
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

    static Lock lock = new ReentrantLock();

    public static void onDatapacksReloaded() {
        try {
            lock.lock();
            try {
                //  Database.backup();
                Database.checkGuidValidity();
                Database.unregisterInvalidEntries();
                Database.getAllRegistries().forEach(x -> x.onAllDatapacksLoaded());
                ExileEvents.AFTER_DATABASE_LOADED.callEvents(new ExileEvents.AfterDatabaseLoaded());
            } finally {
                lock.unlock();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
