package com.robertx22.library_of_exile.database.mob_list;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.tags.tag_types.RegistryTag;

public class MobListTag extends RegistryTag<MobList> {
    public MobListTag(String modid, String id) {
        super(modid, id);
    }

    @Override
    public MobListTag fromTagString(String tag) {
        return new MobListTag(modid, tag);
    }

    @Override
    public ExileRegistryType getRegType() {
        return LibDatabase.MOB_LIST;
    }
}
