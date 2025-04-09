package com.robertx22.library_of_exile.registry.helpers;

import com.robertx22.library_of_exile.events.base.EventConsumer;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.registry.ExileRegistryEvent;
import com.robertx22.library_of_exile.registry.ExileRegistryEventClass;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// used to know how to register stuff properly without headaches
// Place it right at start of mod constructor
public abstract class OrderedModConstructor {

    //  public static HashMap<String, OrderedModConstructor> all = new HashMap<>();

    String modid;

    static Lock lock = new ReentrantLock();

    public static void register(OrderedModConstructor c, IEventBus modbus) {

        try {
            lock.lock();

            c.registerDeferredContainers(modbus);
            c.registerDeferredEntries();
            c.registerDatabases();

            final boolean[] done = {false};
            ExileEvents.EXILE_REGISTRY_GATHER.register(new EventConsumer<ExileRegistryEvent>() {
                @Override
                public void accept(ExileRegistryEvent e) {
                    if (!done[0]) {
                        c.registerDatabaseEntries();
                        done[0] = true;
                    }
                }
            });

            for (ExileRegistryEventClass event : c.getRegisterEvents()) {
                event.register();
            }
            //registerDatabaseEntries();

            for (ExileKeyHolder holder : c.getAllKeyHolders()) {
                holder.init();
            }
            // all.put(modid, this);
        } finally {
            lock.unlock();
        }
    }

    public OrderedModConstructor(String modid) {
        this.modid = modid;

        ExileLog.get().log("Mod Constructor Registered : " + modid);

    }

    public abstract List<ExileRegistryEventClass> getRegisterEvents();

    public abstract List<ExileKeyHolder> getAllKeyHolders();

    public abstract void registerDeferredContainers(IEventBus bus);

    public abstract void registerDeferredEntries();

    public abstract void registerDatabases();

    public abstract void registerDatabaseEntries();

}
