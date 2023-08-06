package com.robertx22.library_of_exile.utils;

import com.robertx22.library_of_exile.registry.IGUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;


public class ItemstackDataSaver<T> implements IGUID {

    String id;
    Class<T> clazz;
    Supplier<T> constructor;

    public ItemstackDataSaver(String id, Class<T> clazz, Supplier<T> constructor) {
        this.id = id;
        this.clazz = clazz;
        this.constructor = constructor;

        if (AllItemStackSavers.ALL.stream()
                .noneMatch(x -> x.GUID()
                        .equals(GUID()))) {
            AllItemStackSavers.ALL.add(this);
        }

    }

    public Supplier<T> getConstructor() {
        return constructor;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public boolean has(ItemStack stack) {
        return stack != null && stack.hasTag() && stack.getTag()
                .contains(id);
    }

    public T loadFrom(ItemStack stack) {

        if (stack == null) {
            return null;
        }
        if (!stack.hasTag()) {
            return null;
        }

        T object = LoadSave.Load(clazz, constructor.get(), stack.getTag(), id);

        return object;

    }

    public void saveTo(ItemStack stack, T object) {
        this.saveToObject(stack, object);
    }

    public void saveToObject(ItemStack stack, Object object) {
        if (stack == null) {
            return;
        }

        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }
        if (object != null) {
            LoadSave.Save(object, stack.getTag(), id);
        }

    }

    @Override
    public String GUID() {
        return id;
    }
}
