package com.robertx22.library_of_exile.util;

import com.robertx22.library_of_exile.events.base.EventConsumer;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.registry.ExileRegistry;

import java.util.HashMap;

// because gson can be finicky, why not use statics instead and save per id and reg type
public abstract class LazyStaticContainer<DATA, OBJECT extends ExileRegistry> {

    public LazyStaticContainer(boolean resetOnDatabase) {

        if (resetOnDatabase) {
            ExileEvents.ON_REGISTER_TO_DATABASE.register(new EventConsumer<ExileEvents.OnRegisterToDatabase>() {
                @Override
                public void accept(ExileEvents.OnRegisterToDatabase onRegisterToDatabase) {
                    wipe = true;
                }
            });
        }
    }

    private boolean wipe = false;

    public abstract DATA createData(OBJECT obj);

    private HashMap<String, HashMap<String, DATA>> map = new HashMap<>();

    public DATA get(OBJECT obj) {
        if (wipe) {
            map = new HashMap<>();
            wipe = false;
        }

        if (!map.containsKey(obj.getExileRegistryType().id)) {
            map.put(obj.getExileRegistryType().id, new HashMap<>());
        }

        var m = map.get(obj.getExileRegistryType().id);

        if (!m.containsKey(obj.GUID())) {
            m.put(obj.GUID(), createData(obj));
        }
        return m.get(obj.GUID());
    }

}
