package com.robertx22.library_of_exile.database.mob_list;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.database.init.PredeterminedResult;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.tags.ExileTagList;
import com.robertx22.library_of_exile.tags.ITaggable;
import com.robertx22.library_of_exile.tags.tag_types.RegistryTag;
import com.robertx22.library_of_exile.utils.RandomUtils;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobList implements JsonExileRegistry<MobList>, IAutoGson<MobList>, ITaggable<MobListTag> {

    public static MobList SERIALIZER = new MobList();

    public static PredeterminedResult<MobList> PREDETERMINED = new PredeterminedResult<MobList>() {
        @Override
        public ExileRegistryType getRegistryType() {
            return LibDatabase.MOB_LIST;
        }

        @Override
        public MobList getPredeterminedRandomINTERNAL(Random random, Level level, ChunkPos pos) {
            return LibDatabase.MobLists().random(random.nextDouble());
        }
    };


    public String id = "";
    public int weight = 1000;
    public List<MobEntry> mobs = new ArrayList<>();

    public MobEntry getRandomMob() {
        return RandomUtils.weightedRandom(mobs);
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
