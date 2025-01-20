package com.robertx22.library_of_exile.main;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.database.map_data_block.LibMapDataBlocks;
import com.robertx22.library_of_exile.database.mob_list.MobLists;
import com.robertx22.library_of_exile.registry.ExileRegistryEventClass;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.OrderedModConstructor;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Arrays;
import java.util.List;

public class LibModConstructor extends OrderedModConstructor {
    public LibModConstructor(String modid, IEventBus modbus) {
        super(modid, modbus);
    }

    @Override
    public List<ExileRegistryEventClass> getRegisterEvents() {
        return Arrays.asList();
    }

    @Override
    public List<ExileKeyHolder> getAllKeyHolders() {
        return Arrays.asList(
                LibMapDataBlocks.INSTANCE,
                MobLists.INSTANCE
        );
    }

    @Override
    public void registerDeferredContainers(IEventBus bus) {
        CommonInit.initDeferred();
    }

    @Override
    public void registerDeferredEntries() {
        ExileLibEntries.init();
    }

    @Override
    public void registerDatabases() {
        LibDatabase.INSTANCE.initDatabases();
    }

    @Override
    public void registerDatabaseEntries() {
        LibDatabase.INSTANCE.registerGatherEvents();
    }
}
