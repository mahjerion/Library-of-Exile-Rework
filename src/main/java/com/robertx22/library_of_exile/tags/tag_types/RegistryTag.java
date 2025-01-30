package com.robertx22.library_of_exile.tags.tag_types;

import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.tags.ExileTag;

import java.util.function.Supplier;

// i think only this one is required really
public class RegistryTag<T extends ExileRegistry<?>> extends ExileTag {

    public Supplier<ExileRegistryType> type;

    public RegistryTag(String modid, String id, Supplier<ExileRegistryType> type) {
        super(modid, id);
        this.type = type;
    }

    @Override
    public String getTagType() {
        return type.get().idWithoutModid;
    }

    @Override
    public ExileTag fromTagString(String tag) {
        return new RegistryTag<T>("", tag, type);
    }
}
