package com.robertx22.library_of_exile.registry;

import com.google.gson.JsonObject;

public interface ExileRegistry<C> extends IGUID, IWeighted {

    ExileRegistryType getExileRegistryType();


    default void registerToExileRegistry() {
        Database.getRegistry(getExileRegistryType())
                .register(this);
    }

    default void compareLoadedJsonAndFinalClass(JsonObject json, Boolean editmode) {
    }

    default void unregisterFromExileRegistry() {
        Database.getRegistry(getExileRegistryType()).unRegister(this);
    }


    default boolean isEmpty() {
        var db = Database.getRegistry(getExileRegistryType());
        var em = db.getDefault();

        if (em != null) {
            if (em.GUID().equals(GUID())) {
                return true;
            }
        }
        return db.isRegistered(GUID());
    }

    default void unregisterDueToInvalidity() {
        Database.getRegistry(getExileRegistryType())
                .unRegister(this);
        try {
            throw new Exception("Registry Entry: " + GUID() + " of type: " + this.getExileRegistryType()
                    .id + " is invalid! Unregistering");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    default boolean isFromDatapack() {
        return false;
    }

    default boolean isRegistryEntryValid() {
        // override with an implementation of a validity test
        return true;
    }

    default String getInvalidGuidMessage() {
        return "Non [a-z0-9_.-] character in Mine and Slash GUID: " + GUID() + " of type " + getExileRegistryType().id;
    }

}
