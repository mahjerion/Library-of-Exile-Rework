package com.robertx22.library_of_exile.vanilla_util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class VanillaRegistryWrapper<T> {


    private Registry<T> reg;

    public VanillaRegistryWrapper(Registry<T> reg) {
        this.reg = reg;
    }

    public T get(ResourceLocation id) {
        return reg.get(id);
    }

    public List<T> getAll() {
        return reg.stream().toList();
    }

    public ResourceLocation getKey(T obj) {
        return reg.getKey(obj);
    }

}
