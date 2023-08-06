package com.robertx22.library_of_exile.registry;

import com.robertx22.library_of_exile.main.LibraryOfExile;

public class DataGenKey<T> implements IGUID {

    String id;

    public DataGenKey(String id) {
        errorIfNotDevMode();
        this.id = id;
    }

    private void errorIfNotDevMode() {
        if (!LibraryOfExile.runDevTools()) {
            throw new RuntimeException("Do not use DataGenKeys outside of dev mode! These keys should only be used when generating data.");
        }
    }

    @Override
    public String GUID() {
        errorIfNotDevMode();
        return id;
    }
}
