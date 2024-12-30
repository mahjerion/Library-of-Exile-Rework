package com.robertx22.library_of_exile.registry.util;

import com.robertx22.library_of_exile.registry.ExileRegistry;

import java.util.HashMap;
import java.util.function.Supplier;

public class ExileExtraData<T> {

    Supplier<T> def;

    HashMap<String, T> map = new HashMap<>();

    public ExileExtraData(Supplier<T> def) {
        this.def = def;
    }

    public void set(ExileRegistry obj, T data) {
        String id = obj.getExileRegistryType().id + ":" + obj.GUID();
        map.put(id, data);
    }

    public T get(ExileRegistry obj) {
        if (!map.containsKey(obj.GUID())) {
            map.put(obj.GUID(), def.get());
        }
        return map.get(obj.GUID());
    }


}
