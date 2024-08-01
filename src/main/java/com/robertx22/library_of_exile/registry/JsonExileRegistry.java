package com.robertx22.library_of_exile.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.robertx22.library_of_exile.registry.serialization.ISerializable;

import java.util.HashSet;
import java.util.Map;

public interface JsonExileRegistry<T> extends ExileRegistry<T> {

    default void addToSerializables() {
        Database.getRegistry(getExileRegistryType())
                .addSerializable(this);
    }

    @Override
    default void compareLoadedJsonAndFinalClass(JsonObject json, Boolean editmode) {
        if (this instanceof ISerializable ser) {
            var after = ser.toJson();

            if (editmode) {
                //if the json only edits some values, we only check those values
                for (Map.Entry<String, JsonElement> en : new HashSet<>(after.entrySet())) {
                    if (json.has(en.getKey())) {
                        after.remove(en.getKey());
                    }
                }
                return;
            }
            if (!json.equals(after)) {
                System.out.println("[Mine and Slash Datapack Warning]: " + this.GUID() + " is different ");
                System.out.println("Json from your datapack:\n");
                System.out.println(json.toString());
                System.out.println("Json after it was loaded and turned back into json:\n");
                System.out.println(after.toString());
                System.out.println("\nPlease check for things like wrong field names, missing fields, wrong types used etc.");
                System.out.println("You can copy and paste these jsons into any online Json Comparison/Diff tools see what the difference is. Like: www.jsondiff.com");
            }

        }
    }

    @Override
    default boolean isFromDatapack() {
        return true;
    }

}
