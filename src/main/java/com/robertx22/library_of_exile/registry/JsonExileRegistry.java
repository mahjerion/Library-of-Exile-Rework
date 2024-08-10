package com.robertx22.library_of_exile.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.registry.serialization.ISerializable;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface JsonExileRegistry<T> extends ExileRegistry<T> {

    default void addToSerializables() {
        Database.getRegistry(getExileRegistryType())
                .addSerializable(this);
    }


    public static HashMap<ExileRegistryType, Set<String>> INVALID_JSONS_MAP = new HashMap<ExileRegistryType, Set<String>>();

    // this is never called because mc already errors for invalid json syntax
    public static HashMap<ExileRegistryType, Set<ResourceLocation>> NOT_LOADED_JSONS_MAP = new HashMap<ExileRegistryType, Set<ResourceLocation>>();

    public static void addToInvalidJsons(ExileRegistryType type, String id) {

        if (!INVALID_JSONS_MAP.containsKey(type)) {
            INVALID_JSONS_MAP.put(type, new HashSet<>());
        }
        INVALID_JSONS_MAP.get(type).add(id);
    }

    public static void addToErroredJsons(ExileRegistryType type, ResourceLocation id) {

        if (!NOT_LOADED_JSONS_MAP.containsKey(type)) {
            NOT_LOADED_JSONS_MAP.put(type, new HashSet<>());
        }
        NOT_LOADED_JSONS_MAP.get(type).add(id);
    }

    ExileLog LOGGER = ExileLog.get();

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
            var v1 = JsonParser.parseString(json.toString());
            var v2 = JsonParser.parseString(after.toString());

            if (!v1.equals(v2)) {
                LOGGER.warn("============[Mine and Slash Datapack Check Failed]=================");
                LOGGER.warn("The file with id " + this.GUID() + " is different after loading");
                LOGGER.warn("Json from your datapack:");
                LOGGER.warn(json.toString());
                LOGGER.warn("Json after it was loaded and turned back into json:");
                LOGGER.warn(after.toString());
                LOGGER.warn("Please check for things like wrong field names, missing fields, wrong types used etc.");
                LOGGER.warn("You can copy and paste these jsons into any online Json Comparison/Diff tools see what the difference is. Like: www.jsondiff.com");
                LOGGER.warn("===================================================================");
                addToInvalidJsons(getExileRegistryType(), GUID());
            }

        }
    }

    @Override
    default boolean isFromDatapack() {
        return true;
    }

}
