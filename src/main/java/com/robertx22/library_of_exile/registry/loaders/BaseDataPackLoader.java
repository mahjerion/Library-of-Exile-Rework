package com.robertx22.library_of_exile.registry.loaders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.registry.ExileRegistryContainer;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.serialization.ISerializable;
import com.robertx22.library_of_exile.utils.Watch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;
import java.util.Objects;

public class BaseDataPackLoader<T extends ExileRegistry> extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    public String id;
    ISerializable<T> serializer;
    public ExileRegistryType registryType;

    public BaseDataPackLoader(ExileRegistryType registryType, String id, ISerializable<T> serializer) {
        super(GSON, id);
        Objects.requireNonNull(registryType);
        this.id = id;
        this.serializer = serializer;
        this.registryType = registryType;
    }


    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager manager, ProfilerFiller profiler) {


        return super.prepare(manager, profiler);
    }

    static String ENABLED = "enabled";

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> mapToLoad, ResourceManager manager, ProfilerFiller profilerIn) {

        try {
            ExileRegistryContainer reg = Database.getRegistry(registryType);

            Watch normal = new Watch();
            normal.min = 50000;

            mapToLoad.forEach((key, value) -> {
                try {
                    JsonObject json = value.getAsJsonObject();
                    T object = serializer.fromJson(json);


                    if (Database.getRegistry(registryType).isRegistered(object.GUID())) {
                        T existing = (T) Database.getRegistry(registryType).get(object.GUID());
                        ISerializable<T> exSer = (ISerializable<T>) existing;
                        JsonObject existingJson = exSer.toJson();

                        for (Map.Entry<String, JsonElement> en : json.entrySet()) {
                            existingJson.add(en.getKey(), en.getValue());
                        }
                        object = this.serializer.fromJson(existingJson);
                    }

                    if (!json.has(ENABLED) || json.get(ENABLED).getAsBoolean()) {
                        object.unregisterFromExileRegistry();
                        object.registerToExileRegistry();
                    }

                } catch (Exception exception) {
                    System.out.println(id + " is a broken datapack entry.");
                    exception.printStackTrace();
                }
            });

            normal.print("Loading " + registryType.id + " jsons ");

            if (reg
                    .isEmpty()) {
                throw new RuntimeException("Exile Registry of type " + registryType.id + " is EMPTY after datapack loading!");
            } else {
                // System.out.println(registryType.name() + " Registry succeeded loading: " + reg.getSize() + " datapack entries.");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

}