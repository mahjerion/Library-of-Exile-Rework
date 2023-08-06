package com.robertx22.library_of_exile.registry.serialization;

public interface ISerializablePart<T> extends ISerializable<T> {
    String getJsonID();
}
