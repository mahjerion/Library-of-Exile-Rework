package com.robertx22.library_of_exile.tags;

import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.ITranslated;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.IGUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ExileTag implements ITranslated, IGUID {

    public static HashMap<String, List<ExileTag>> MAP = new HashMap<>();

    public String modid;
    public String id;
    public String type;

    public ExileTag(String modid, String id, String type) {
        this.modid = modid;
        this.id = id;

        if (!modid.isEmpty()) {
            if (!MAP.containsKey(type)) {
                MAP.put(type, new ArrayList<>());
            }
            MAP.get(type).add(this);
        }
    }


    public abstract ExileTag fromTagString(String tag);

    @Override
    public TranslationBuilder createTranslationBuilder() {
        String name = capitalise(GUID().replaceAll("_", " "));
        return TranslationBuilder.of(modid).name(ExileTranslation.of(Ref.MODID + ".tag." + type + "." + GUID(), name));
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
}
