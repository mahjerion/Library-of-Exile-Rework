package com.robertx22.library_of_exile.database.mob_list;

import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.tags.tag_types.RegistryTag;

public class MobListTags {

    public static RegistryTag<MobList> HAS_FLYING_MOBS = of("has_flying_mobs");
    public static RegistryTag<MobList> HARVEST = of("harvest");
    public static RegistryTag<MobList> MAP = of("map");
    public static RegistryTag<MobList> FOREST = of("forest");
    public static RegistryTag<MobList> OBELISK = of("obelisk");

    // this is boilerplate every mod needs for the tags they use, hm
    private static RegistryTag<MobList> of(String id) {
        return new MobListTag(Ref.MODID, id);
    }
}
