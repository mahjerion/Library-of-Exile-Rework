package com.robertx22.library_of_exile.database.mob_list;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.tags.tag_types.RegistryTag;

public class MobListTags {

    public static RegistryTag<MobList> HAS_FLYING_MOBS = of("has_flying_mobs");

    // this is boilerplate every mod needs for the tags they use, hm
    private static RegistryTag<MobList> of(String id) {
        return new RegistryTag<>(Ref.MODID, id, LibDatabase.MOB_LIST.idWithoutModid);
    }
}
