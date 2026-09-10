package com.robertx22.library_of_exile.registry.loaders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.registry.*;
import com.robertx22.library_of_exile.registry.register_info.FromDatapackRegistration;
import com.robertx22.library_of_exile.registry.serialization.ISerializable;
import com.robertx22.library_of_exile.utils.Watch;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.IOException;
import java.io.Reader;
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


    /**
     * Like vanilla's scanDirectory, but a real datapack always beats a mod jar for the same file.
     * <p>
     * Vanilla resolves each file to the highest enabled pack. That order lives in the world's level.dat,
     * and Forge appends any mod that joined the world after its creation to the END of that list - on top
     * of every datapack, OpenLoader folders included. A pack author's override of a registry entry then
     * silently loses to the jar's own generated json in exactly the worlds that got the mod later, while
     * working everywhere else. A mod jar's json is only the shipped default and a datapack is always
     * deliberate, so for these registries the world's pack order must not decide: take the highest
     * non-built-in source when there is one, else the highest source as before. Only Exile registries go
     * through here - vanilla folders (recipes, loot tables, tags...) keep vanilla's rules.
     * <p>
     * "Built-in" is the flag Forge sets on every mod jar pack (ResourcePackLoader.createPackForMod) and
     * vanilla sets on its own data; world datapacks and OpenLoader folders are created with it false. The
     * pack id is no use here - Forge's is the jar file name, "mod:modid" is only the repository entry.
     */
    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, JsonElement> map = new HashMap<>();
        FileToIdConverter converter = FileToIdConverter.json(this.id);
        int takenFromDatapacks = 0;

        for (Map.Entry<ResourceLocation, List<Resource>> entry : converter.listMatchingResourceStacks(manager).entrySet()) {
            ResourceLocation file = entry.getKey();
            ResourceLocation key = converter.fileToId(file);
            List<Resource> stack = entry.getValue(); // lowest pack first, highest last
            if (stack.isEmpty()) {
                continue;
            }

            Resource chosen = stack.get(stack.size() - 1);
            for (int i = stack.size() - 1; i >= 0; i--) {
                Resource res = stack.get(i);
                if (!res.isBuiltin()) {
                    if (res != chosen) {
                        takenFromDatapacks++;
                    }
                    chosen = res;
                    break;
                }
            }

            try (Reader reader = chosen.openAsReader()) {
                map.put(key, GsonHelper.fromJson(GSON, reader, JsonElement.class));
            } catch (IllegalArgumentException | IOException | JsonParseException e) {
                ExileLog.get().error("Couldn't parse data file " + key + " from " + file + " (pack " + chosen.sourcePackId() + ")", e);
            }
        }

        if (takenFromDatapacks > 0) {
            ExileLog.get().log(takenFromDatapacks + " " + this.id + " entries taken from datapacks over built-in mod packs placed above them in the world's pack order");
        }
        return map;
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
                        System.err.println("Failed to parse file: " + entry.getKey());
                        e.printStackTrace();

                        String info = getInfoString(entry.getKey(), LoaderType.ERROR_LOADING);
                        INFO_MAP.get(registryType).add(info);

                        continue;
                    }

                    if (object == null) {
                        // serializer not registered (e.g. its owning mod/addon isn't loaded) -
                        // already logged by GsonCustomSer.fromJson, just skip this entry
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
                        object.registerToExileRegistry(new FromDatapackRegistration(key));

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
                    ExileLog.get().warn(key.toString() + " is a broken datapack entry.");
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