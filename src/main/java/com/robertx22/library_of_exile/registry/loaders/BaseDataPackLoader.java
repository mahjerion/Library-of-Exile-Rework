package com.robertx22.library_of_exile.registry.loaders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.robertx22.library_of_exile.registry.*;
import com.robertx22.library_of_exile.registry.serialization.ISerializable;
import com.robertx22.library_of_exile.utils.Watch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;

public class BaseDataPackLoader<T extends ExileRegistry> extends SimpleJsonResourceReloadListener {
    private static Gson GSON = IAutoGson.createGson();


    public String id;
    ISerializable<T> serializer;
    public ExileRegistryType registryType;


    public static HashMap<ExileRegistryType, List<String>> INFO_MAP = new HashMap<>();

    public BaseDataPackLoader(ExileRegistryType registryType, String id, ISerializable<T> serializer) {
        super(GSON, id);
        Objects.requireNonNull(registryType);
        this.id = id;
        this.serializer = serializer;
        this.registryType = registryType;
    }

    public enum LoaderType {
        REPLACE_FULLY, REPLACE_FIELDS, ERROR_LOADING
    }


    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager manager, ProfilerFiller profiler) {


        return super.prepare(manager, profiler);
    }

    public static String ENABLED = "enabled";
    public static String LOADER = "loader";

    String getInfoString(ResourceLocation key, LoaderType type) {
        return key.getNamespace() + ":" + key.getPath() + ":" + type.name();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> mapToLoad, ResourceManager manager, ProfilerFiller profilerIn) {

        try {
            ExileRegistryContainer reg = Database.getRegistry(registryType);

            Watch normal = new Watch();
            normal.min = 50000;

            INFO_MAP.put(registryType, new ArrayList<>());

            for (Map.Entry<ResourceLocation, JsonElement> entry : mapToLoad.entrySet()) {
                ResourceLocation key = entry.getKey();
                JsonElement value = entry.getValue();
                try {

                    JsonObject json = null;
                    T object = null;
                    try {
                        json = value.getAsJsonObject();
                        object = serializer.fromJson(json);
                    } catch (Exception e) {
                        e.printStackTrace();

                        String info = getInfoString(entry.getKey(), LoaderType.ERROR_LOADING);
                        INFO_MAP.get(registryType).add(info);

                        continue;
                    }


                    LoaderType type = LoaderType.REPLACE_FULLY;

                    if (json.has(LOADER)) {
                        try {
                            type = LoaderType.valueOf(json.get(LOADER).getAsString());
                        } catch (IllegalArgumentException e) {
                            type = LoaderType.REPLACE_FULLY;
                        }
                    }

                    if (!Database.getRegistry(registryType).isExistingSeriazable(object.GUID())) {
                        //type = LoaderType.NEW;
                    } else {
                        if (type == LoaderType.REPLACE_FIELDS && Database.getRegistry(registryType).isExistingSeriazable(object.GUID())) {
                            T existing = (T) Database.getRegistry(registryType).get(object.GUID());
                            ISerializable<T> exSer = (ISerializable<T>) existing;
                            JsonObject existingJson = exSer.toJson();

                            for (Map.Entry<String, JsonElement> en : json.entrySet()) {
                                existingJson.add(en.getKey(), en.getValue());
                            }
                            object = this.serializer.fromJson(existingJson);
                        }
                    }

                    if (!json.has(ENABLED) || json.get(ENABLED).getAsBoolean()) {
                        object.unregisterFromExileRegistry();
                        object.registerToExileRegistry();

                        String infostring = getInfoString(key, type);
                        INFO_MAP.get(registryType).add(infostring);
                    }

                    if (json.has(ENABLED)) {
                        if (!json.get(ENABLED).getAsBoolean()) {
                            object.unregisterFromExileRegistry();
                        }
                    }

                    if (object != null) {
                        JsonObject compare = json.deepCopy();
                        compare.remove(ENABLED);
                        object.compareLoadedJsonAndFinalClass(compare, type == LoaderType.REPLACE_FIELDS);
                    }

                } catch (Exception exception) {
                    System.out.println(key.toString() + " is a broken datapack entry.");
                    JsonExileRegistry.addToErroredJsons(registryType, key);
                    exception.printStackTrace();
                }
            }

            normal.print("Loading " + registryType.id + " jsons ");

            if (reg.isEmpty()) {
                throw new RuntimeException("Exile Registry of type " + registryType.id + " is EMPTY after datapack loading!");
            } else {
                // System.out.println(registryType.name() + " Registry succeeded loading: " + reg.getSize() + " datapack entries.");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

}