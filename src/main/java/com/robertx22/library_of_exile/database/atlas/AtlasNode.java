package com.robertx22.library_of_exile.database.atlas;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;

import java.util.ArrayList;
import java.util.List;

// A node in the Atlas map graph. Maps a dungeon layout (by GUID, resolved by whichever
// module owns dungeons - not validated here to avoid a reverse module dependency) to a
// position in the atlas grid and a set of neighboring nodes. Clearing the linked dungeon
// unlocks the neighbors for that player. See AtlasNodeUtils for lookups.
public class AtlasNode implements JsonExileRegistry<AtlasNode>, IAutoGson<AtlasNode> {

    public static AtlasNode SERIALIZER = new AtlasNode();

    public String id = "";
    public String dungeon = "";
    public int x = 0;
    public int y = 0;
    public List<String> neighbors = new ArrayList<>();
    public boolean starting_node = false;
    public int atlas_points_reward = 1;

    @Override
    public ExileRegistryType getExileRegistryType() {
        return LibDatabase.ATLAS_NODE;
    }

    @Override
    public Class<AtlasNode> getClassForSerialization() {
        return AtlasNode.class;
    }

    @Override
    public String GUID() {
        return id;
    }

    @Override
    public int Weight() {
        return 1000;
    }

    @Override
    public boolean isRegistryEntryValid() {
        for (String neighbor : neighbors) {
            if (!LibDatabase.AtlasNodes().isRegistered(neighbor)) {
                ExileLog.get().warn("AtlasNode " + id + " references unknown neighbor node: " + neighbor);
                return false;
            }
        }
        return true;
    }
}
