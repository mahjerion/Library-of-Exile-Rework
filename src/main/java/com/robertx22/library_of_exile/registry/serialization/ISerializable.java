package com.robertx22.library_of_exile.registry.serialization;

import com.google.gson.JsonObject;
import com.robertx22.library_of_exile.registry.IGUID;
import com.robertx22.library_of_exile.registry.IWeighted;

public interface ISerializable<T> {
    JsonObject toJson();

    T fromJson(JsonObject json);

    static String ID = "id";
    static String WEIGHT = "weight";

    default String datapackFolder() {
        return "";
    }

    default String getGUIDFromJson(JsonObject json) {
        return json.get(ID)
            .getAsString();
    }

    default int getWeightFromJson(JsonObject json) {
        return json.get(WEIGHT)
            .getAsInt();
    }

    default JsonObject getDefaultJson() {
        JsonObject json = new JsonObject();

        if (this instanceof IGUID) {
            IGUID claz = (IGUID) this;
            json.addProperty(ID, claz.GUID());
        }

        if (this instanceof IWeighted) {
            IWeighted claz = (IWeighted) this;
            json.addProperty(WEIGHT, claz.Weight());
        }

        return json;
    }

    default String toJsonString() {
        return toJson().toString();
    }

    default boolean shouldGenerateJson() {
        return true;
    }

}
