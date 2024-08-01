package com.robertx22.library_of_exile.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.registry.serialization.ISerializable;

public interface IAutoGson<T extends IAutoGson<T>> extends ISerializable<T> {

    public static Gson GSON = createGson();

    JsonParser PARSER = new JsonParser();

    public static Gson createGson() {
        var b = new GsonBuilder();
        ExileEvents.DATAPACK_GSON_ADAPTER_REGISTRY.callEvents(new ExileEvents.DatapackGsonAdapterEvent(b));
        return b.create();
    }

    @Override
    default JsonObject toJson() {
        return PARSER.parse(GSON.toJson(this)).getAsJsonObject();
    }

    @Override
    default String toJsonString() {
        return GSON.toJson(this);
    }

    default void onLoadedFromJson() {

    }

    @Override
    default T fromJson(JsonObject json) {
        T t = GSON.fromJson(json, getClassForSerialization());
        t.onLoadedFromJson();
        return t;
    }

    Class<T> getClassForSerialization();

}
