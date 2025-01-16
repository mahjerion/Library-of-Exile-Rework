package com.robertx22.library_of_exile.util;


import java.util.function.Supplier;

public class LazyClass<T> {

    private T obj;

    private Supplier<T> sup;

    public LazyClass(Supplier<T> sup) {
        this.sup = sup;
    }

    public T get() {
        if (obj == null) {
            obj = sup.get();
        }
        return obj;
    }
}
