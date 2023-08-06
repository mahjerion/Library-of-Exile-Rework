package com.robertx22.library_of_exile.registry;

public interface JsonExileRegistry<T> extends ExileRegistry<T> {

    default void addToSerializables() {
        Database.getRegistry(getExileRegistryType())
            .addSerializable(this);
    }

    @Override
    default boolean isFromDatapack() {
        return true;
    }

}
