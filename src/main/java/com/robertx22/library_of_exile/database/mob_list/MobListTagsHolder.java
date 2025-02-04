package com.robertx22.library_of_exile.database.mob_list;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.tags.ExileTagList;
import com.robertx22.library_of_exile.tags.tag_types.RegistryTag;

public class MobListTagsHolder extends ExileTagList<RegistryTag<MobList>> {
    @Override
    public RegistryTag<MobList> getInstance() {
        return (RegistryTag<MobList>) LibDatabase.MOB_LIST.tagType;
    }
}
