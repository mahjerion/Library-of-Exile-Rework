package com.robertx22.library_of_exile.registry;


import com.robertx22.library_of_exile.events.base.EventConsumer;
import com.robertx22.library_of_exile.events.base.ExileEvents;

public abstract class ExileRegistryEventClass extends EventConsumer<ExileRegistryEvent> {


    public abstract ExileRegistryType getType();

    public abstract void init(ExileRegistryEvent e);

    @Override
    public void accept(ExileRegistryEvent e) {
        if (e.type == getType()) {
            init(e);
        }
    }

    public void register() {
        ExileEvents.EXILE_REGISTRY_GATHER.register(this);
    }
}
