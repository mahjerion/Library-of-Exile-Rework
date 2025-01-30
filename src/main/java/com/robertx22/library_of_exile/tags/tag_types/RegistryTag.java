package com.robertx22.library_of_exile.tags.tag_types;

import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.tags.ExileTag;

// i think only this one is required really
public class RegistryTag<T extends ExileRegistry<?>> extends ExileTag {

  
    public RegistryTag(String modid, String id, String type) {
        super(modid, id, type);
    }


    @Override
    public ExileTag fromTagString(String tag) {
        return new RegistryTag<T>("", tag, "");
    }
}
