package com.robertx22.library_of_exile.registry;

import com.google.gson.JsonObject;
import com.robertx22.library_of_exile.registry.register_info.ExileRegistrationInfo;
import com.robertx22.library_of_exile.registry.register_info.RegistrationInfoData;

public interface ExileRegistry<C> extends IGUID, IWeighted {

    ExileRegistryType getExileRegistryType();


    default void registerToExileRegistry(ExileRegistrationInfo info) {
        Database.getRegistry(getExileRegistryType()).register(this, info);
    }

    default RegistrationInfoData getRegistrationInfo() {
        return (RegistrationInfoData) Database.getRegistry(getExileRegistryType()).registrationInfo.get(this.GUID());
    }

    default void compareLoadedJsonAndFinalClass(JsonObject json, Boolean editmode) {
    }

    default void unregisterFromExileRegistry() {
        Database.getRegistry(getExileRegistryType()).unRegister(this);
    }

    default String getRegistryIdPlusGuid() {
        return getExileRegistryType().id + ":" + GUID();
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
