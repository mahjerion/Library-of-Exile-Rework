package com.robertx22.library_of_exile.custom_ser;

import java.util.HashMap;

public class CustomSerializer<T extends ICustomSer<T>> {

    public HashMap<String, T> map = new HashMap<>();

    public void register(T t) {
        map.put(t.getSer(), t);
    }
}
