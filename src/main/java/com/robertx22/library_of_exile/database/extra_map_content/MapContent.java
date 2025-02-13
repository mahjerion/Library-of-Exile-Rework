package com.robertx22.library_of_exile.database.extra_map_content;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;

// todo do this
// Used to spawn extra content in maps, typically a block
public class MapContent implements IAutoGson<MapContent>, JsonExileRegistry<MapContent> {

    public static MapContent SERIALIZER = new MapContent();

    public static MapContent of(String id, int weight, String block, int min, int max) {
        MapContent m = new MapContent();
        m.id = id;
        m.weight = weight;
        m.block_id = block;
        m.max_blocks = max;
        m.min_blocks = min;
        return m;
    }

    public int weight = 1000;
    public String id = "";
    public String block_id = "";
    public int min_blocks = 1;
    public int max_blocks = 1;

    public boolean always_spawn = false;

    @Override
    public ExileRegistryType getExileRegistryType() {
        return LibDatabase.MAP_CONTENT;
    }

    @Override
    public Class<MapContent> getClassForSerialization() {
        return MapContent.class;
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
