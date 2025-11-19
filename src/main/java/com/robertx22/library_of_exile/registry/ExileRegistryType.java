package com.robertx22.library_of_exile.registry;

import com.google.common.base.Preconditions;
import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.registry.loaders.BaseDataPackLoader;
import com.robertx22.library_of_exile.registry.serialization.ISerializable;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.ModList;

import java.util.*;
import java.util.stream.Collectors;

public class ExileRegistryType<T extends ExileRegistry<T>> {

    private static final HashMap<String, ExileRegistryType<?>> map = new HashMap<>();

    public String id;
    ISerializable<T> ser;
    int order;
    public SyncTime syncTime;
    public String modid;
    // used for lang file tc
    public String idWithoutModid;

    public ExileRegistryType(String modid, String id, int order, ISerializable<T> ser, SyncTime synctime) {

        Preconditions.checkNotNull(modid);
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(synctime);

        this.modid = modid;
        this.idWithoutModid = id;
        this.id = modid + "_" + id;
        this.order = order;
        this.ser = ser;
        this.syncTime = synctime;
    }

    public String getModName() {
        return ModList.get().getModContainerById(modid).get().getModInfo().getDisplayName();
    }

    public static ExileRegistryType<?> get(String id) {
        return map.get(id);
    }

    public static <C extends ExileRegistry<C>> ExileRegistryType<C> register(ExileRegistryType<C> type) {
        Preconditions.checkNotNull(type);

        if (map.containsKey(type.id)) {
            ExileLog.get().warn("Duplicate ExileRegistryType: " + type.id);
        }
        map.put(type.id, type);

        return type;
    }

    public static <C extends ExileRegistry<C>> ExileRegistryType<C> register(String modid, String id, int order, ISerializable<C> ser, SyncTime synctime) {
        ExileRegistryType<C> type = new ExileRegistryType<>(modid, id, order, ser, synctime);
        return register(type);
    }

    public static List<ExileRegistryType<?>> getInRegisterOrder(SyncTime sync) {
        return map.values().stream()
                .filter(x -> x.syncTime == sync)
                .sorted(Comparator.comparingInt(x -> x.order))
                .collect(Collectors.toList());

    }

    public static List<ExileRegistryType<?>> getAllInRegisterOrder() {
        List<ExileRegistryType<?>> list = new ArrayList<>();

        for (Map.Entry<String, ExileRegistryType<?>> en : map.entrySet()) {
            if (en.getValue() == null) {
                throw new RuntimeException(en.getKey() + " is a null registry type, how?!");
            } else {
                list.add(en.getValue());
            }
        }
        list.sort(Comparator.comparingInt(x -> {
            return x.order;
        }));
        return list;
    }


    public static void registerJsonListeners(AddReloadListenerEvent manager) {
        List<ExileRegistryType<?>> list = getAllInRegisterOrder();
        list.forEach(x -> {
            if (x.getLoader() != null) {
                manager.addListener(x.getLoader());
            }
        });
    }


    public BaseDataPackLoader getLoader() {
        if (this.ser == null) {
            return null;
        }
        return new BaseDataPackLoader(this, this.id, this.ser);
    }

    public ExileDatapackGenerator getDatapackGenerator() {
        return new ExileDatapackGenerator<>(modid, getAllForSerialization(), this.id);
    }


    public List getAllForSerialization() {
        return Database.getRegistry(this)
                .getSerializable();
    }

    public final ISerializable<T> getSerializer() {
        return ser;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }
}
