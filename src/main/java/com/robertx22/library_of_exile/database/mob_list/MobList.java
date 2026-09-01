package com.robertx22.library_of_exile.database.mob_list;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.tags.ExileTagList;
import com.robertx22.library_of_exile.tags.ITaggable;
import com.robertx22.library_of_exile.tags.tag_types.RegistryTag;
import com.robertx22.library_of_exile.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class MobList implements JsonExileRegistry<MobList>, IAutoGson<MobList>, ITaggable<MobListTag> {

    public static MobList SERIALIZER = new MobList();


    public String id = "";
    public int weight = 1000;
    public List<MobEntry> mobs = new ArrayList<>();

    /**
     * One mob from this list, weighted.
     * <p>
     * Entries whose mob isn't registered are skipped rather than rolled. A list names mobs by id and
     * those ids belong to other mods - a modpack list routinely references a dozen, and this library
     * ships lists naming mobs from its own addons - so an id that resolves to nothing is a normal
     * consequence of removing a mod, not a data error. Every caller does
     * {@code getRandomMob().getType()} straight into a spawn, so without this filter that missing
     * mod is a crash that waits for the right roll and then hits one room in twenty.
     * <p>
     * Null when nothing on the list resolves. Every caller must null check: dungeon_realm's mob
     * data blocks skip the spawner, and the strongbox / imprisoned monster encounters fall back to a
     * zombie rather than spawning nothing at all.
     */
    public MobEntry getRandomMob() {
        List<MobEntry> present = new ArrayList<>();
        for (MobEntry entry : mobs) {
            if (entry.getType() != null) {
                present.add(entry);
            }
        }
        return RandomUtils.weightedRandom(present);
    }

    // tags
    @Override
    public ExileTagList<MobListTag> getTags() {
        return tags;
    }

    public MobListTagsHolder tags = new MobListTagsHolder();
    // tags

    public MobList(String id, int weight, List<MobEntry> mobs, RegistryTag<MobList>... tags) {
        for (RegistryTag<MobList> tag : tags) {
            this.tags.tags.add(tag.GUID());
        }
        this.id = id;
        this.weight = weight;
        this.mobs = mobs;
    }

    public MobList() {
    }

    @Override
    public ExileRegistryType getExileRegistryType() {
        return LibDatabase.MOB_LIST;
    }

    @Override
    public Class<MobList> getClassForSerialization() {
        return MobList.class;
    }

    @Override
    public String GUID() {
        return id;
    }

    @Override
    public int Weight() {
        return weight;
    }
}
