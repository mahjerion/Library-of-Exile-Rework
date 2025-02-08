package com.robertx22.library_of_exile.util;

import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.registry.ExileRegistryContainer;

import java.util.Optional;

public interface iTiered<T extends ExileRegistry & iTiered<T>> {

    public int getTier();

    default Optional<T> getHigher() {
        ExileRegistryContainer<T> db = Database.getRegistry(self().getExileRegistryType());
        return db.getFiltered(x -> x.getTier() == getTier() + 1).stream().findAny();
    }

    private T self() {
        return (T) this;
    }
}
