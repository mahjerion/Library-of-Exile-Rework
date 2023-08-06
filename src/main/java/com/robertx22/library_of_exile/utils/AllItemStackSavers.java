package com.robertx22.library_of_exile.utils;

import java.util.ArrayList;
import java.util.List;

public class AllItemStackSavers {

    public static List<ItemstackDataSaver> ALL = new ArrayList<>();

    public static <T> List<ItemstackDataSaver<? extends T>> getAllOfClass(Class<T> clazz) {

        List<ItemstackDataSaver<? extends T>> list = new ArrayList<>();

        ALL.stream()
            .filter(x -> clazz.isAssignableFrom(x.getClazz()) || x.getClazz()
                .isAssignableFrom(clazz))
            .forEach(x -> {
                list.add(x);
            });

        return list;

    }

}
