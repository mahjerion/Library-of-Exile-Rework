package com.robertx22.library_of_exile.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Supplier;


public class LoadSave {


    private static final Gson gson = new GsonBuilder().create();

    public static CompoundTag Save(Object obj, CompoundTag nbt, String loc) {


        if (nbt == null) {
            nbt = new CompoundTag();
        }

        String json = null;
        try {
            json = gson.toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (json != null) {
            nbt.putString(loc, json);
        }

        return nbt;

    }

    public static <OBJ> OBJ load(Class<OBJ> clazz, CompoundTag nbt, String loc) {
        OBJ o = null;
        if (nbt == null) {
            return null;
        }

        String json = nbt.getString(loc);

        if (json.isEmpty()) {
            return null;
        }

        try {
            o = gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return o;
    }

    /**
     * @deprecated Replaced with {@link LoadSave#load(Class, CompoundTag, String)}
     */
    @Deprecated(since = "2.2.0", forRemoval = true)
    public static <OBJ> OBJ Load(Class clazz, OBJ newobj, CompoundTag nbt, String loc) {
        return load((Class<OBJ>) clazz, nbt, loc);
    }

    public static <OBJ> OBJ loadOrBlank(Class<OBJ> clazz, CompoundTag nbt, String loc, Supplier<OBJ> blank) {
        try {
            OBJ data = load(clazz, nbt, loc);
            if (data == null) {
                return blank.get();
            } else {
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blank.get();
    }

    /**
     * @deprecated Replaced with {@link  LoadSave#loadOrBlank(Class, CompoundTag, String, Supplier)}
     */
    @Deprecated(since = "2.2.0", forRemoval = true)
    public static <OBJ> OBJ loadOrBlank(Class clazz, OBJ newobj, CompoundTag nbt, String loc, OBJ blank) {
        return loadOrBlank((Class<OBJ>) clazz, nbt, loc, () -> blank);
    }

}
