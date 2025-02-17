package com.robertx22.library_of_exile.database.relic.affix;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.database.relic.stat.RelicMod;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RelicAffix implements JsonExileRegistry<RelicAffix>, IAutoGson<RelicAffix> {
    public static EmptyRelicAffix SERIALIZER = new EmptyRelicAffix("empty");

    public String id;
    public int weight = 1000;

    public String relic_type = "";

    public List<RelicMod> mods = new ArrayList<>();

    public RelicAffix(String id, String relic_type, RelicMod... mods) {
        this.id = id;
        this.relic_type = relic_type;
        this.mods = Arrays.asList(mods);
    }

  
    @Override
    public ExileRegistryType getExileRegistryType() {
        return LibDatabase.RELIC_AFFIX;
    }

    @Override
    public Class<RelicAffix> getClassForSerialization() {
        return RelicAffix.class;
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
