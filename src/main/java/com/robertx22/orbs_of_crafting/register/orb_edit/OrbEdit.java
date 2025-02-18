package com.robertx22.orbs_of_crafting.register.orb_edit;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.registry.helpers.ExileCached;
import com.robertx22.orbs_of_crafting.register.ExileCurrency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrbEdit implements IAutoGson<OrbEdit>, JsonExileRegistry<OrbEdit> {

    public static OrbEdit SERIALIZER = new OrbEdit();

    public String id = "";
    public String orb_id = "";

    public List<ExileCurrency.ItemModData> pick_one_item_mod = new ArrayList<>();
    public List<ExileCurrency.ItemModData> always_do_item_mods = new ArrayList<>();
    public List<String> req = new ArrayList<>();

    public static OrbEdit of(String id, String orb) {
        OrbEdit e = new OrbEdit();
        e.id = id;
        e.orb_id = orb;
        return e;
    }

    public static ExileCached<HashMap<ExileCurrency, List<OrbEdit>>> ORB_AND_EDIT_CACHED_MAP = new ExileCached<>(() -> {
        HashMap<ExileCurrency, List<OrbEdit>> map = new HashMap<>();

        for (ExileCurrency cur : LibDatabase.Currency().getList()) {
            for (OrbEdit edit : LibDatabase.OrbEdits().getList()) {
                if (edit.isFor(cur)) {
                    // todo does the registry work fine like this? hash i mean
                    if (!map.containsKey(cur)) {
                        map.put(cur, new ArrayList<>());
                    }
                    map.get(cur).add(edit);
                }
            }
        }
        return map;
    }).clearOnDatabaseChange();


    public boolean isFor(ExileCurrency cur) {
        return cur.GUID().equals(orb_id);
    }

    @Override
    public ExileRegistryType getExileRegistryType() {
        return LibDatabase.ORB_EDIT;
    }

    @Override
    public Class<OrbEdit> getClassForSerialization() {
        return OrbEdit.class;
    }

    @Override
    public String GUID() {
        return id;
    }

    @Override
    public int Weight() {
        return 1000;
    }
}
