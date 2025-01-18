package com.robertx22.library_of_exile.registry;

import com.robertx22.library_of_exile.events.base.ExileEvent;
import com.robertx22.library_of_exile.registry.register_info.ExileRegistrationInfo;

public class ExileRegistryEvent extends ExileEvent {

    public ExileRegistryType type;

    public ExileRegistryEvent(ExileRegistryType type) {
        this.type = type;
    }

    public void addSeriazable(ExileRegistry<?> en, ExileRegistrationInfo info) {
        if (en instanceof JsonExileRegistry<?> == false) {
            throw new RuntimeException("Not seriazable: " + en.GUID());
        }
        ((JsonExileRegistry<?>) en).addToSerializables(info);
    }

    public void add(ExileRegistry<?> en, ExileRegistrationInfo info) {
        en.registerToExileRegistry(info);
    }
}
