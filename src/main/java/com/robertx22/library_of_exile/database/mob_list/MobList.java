package com.robertx22.library_of_exile.database.mob_list;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.database.init.PredeterminedResult;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MobList implements JsonExileRegistry<MobList>, IAutoGson<MobList> {

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

    public static class Tags {
        public static String CONTAINS_FLYING_MOBS = "has_flying_mobs";
    }


    public List<String> tags = new ArrayList<>();
    public String id = "";
    public int weight = 1000;
    public List<MobEntry> mobs = new ArrayList<>();

    public MobList(String id, int weight, List<MobEntry> mobs, String... tags) {
        this.tags.addAll(Arrays.asList(tags));
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
