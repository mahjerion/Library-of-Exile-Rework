package com.robertx22.library_of_exile.registry.serialization;

import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public interface IGson<T> extends ISerializable<T> {

    default JsonObject toJson() {
        return MyGSON.GSON.toJsonTree(this)
            .getAsJsonObject();
    }

    Type getClassType();

    default T fromJson(JsonObject json) {
        return MyGSON.GSON.fromJson(json, getClassType());
    }

}
