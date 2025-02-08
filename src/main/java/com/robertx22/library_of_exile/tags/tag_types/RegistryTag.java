package com.robertx22.library_of_exile.tags.tag_types;

import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.ITranslated;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IGUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// i think only this one is required really
public abstract class RegistryTag<T extends ExileRegistry<?>> implements ITranslated, IGUID {

    public static HashMap<String, List<RegistryTag>> MAP = new HashMap<>();

    public String modid;
    public String id;

    public RegistryTag(String modid, String id) {
        this.modid = modid;
        this.id = id;


        String type = getRegType().idWithoutModid;

        if (!modid.isEmpty()) {
            if (!MAP.containsKey(type)) {
                MAP.put(type, new ArrayList<>());
            }
            MAP.get(type).add(this);
        }
    }

    public abstract RegistryTag fromTagString(String tag);

    @Override
    public TranslationBuilder createTranslationBuilder() {
        String name = capitalise(GUID().replaceAll("_", " "));
        return TranslationBuilder.of(modid).name(ExileTranslation.of(Ref.MODID + ".tag." + getRegType().idWithoutModid + "." + GUID(), name));
    }

    @Override
    public String GUID() {
        return id;
    }

    public static String capitalise(String str) {
        if (str == null) {
            return null;
        } else if (str.length() == 0) {
            return "";
        } else {
            return new StringBuilder(str.length()).append(Character.toTitleCase(str.charAt(0))).append(str, 1,
                    str.length()).toString();
        }
    }

    public abstract ExileRegistryType getRegType();


}
