package com.robertx22.library_of_exile.database.atlas;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;

// A node in the Atlas map graph. Maps a dungeon layout (by GUID, resolved by whichever
// module owns dungeons - not validated here to avoid a reverse module dependency) to a
// requirement gate. Position and connections between nodes are NOT declared here - they
// live in the main mod's AtlasNodeLayout datapack entry (one grid string referencing node
// ids by this class's `id`, same pattern as Perk/TalentTree), so this class stays purely
// "what is this node" and can't drift out of sync with a hand-declared neighbor list.
// See AtlasNodeUtils for lookups.
public class AtlasNode implements JsonExileRegistry<AtlasNode>, IAutoGson<AtlasNode> {

    public static AtlasNode SERIALIZER = new AtlasNode();

    public String id = "";
    public String dungeon = "";
    public boolean starting_node = false;
    public int atlas_points_reward = 1;

    // optional extra completion requirements, evaluated against the actual map run's rolled
    // attributes - lets the same dungeon appear more than once in the tree (see
    // AtlasNodeUtils.byDungeon), with a later appearance demanding a harder run. 0/empty/false
    // means "no extra requirement". Kept as plain primitives since GearRarity (which min_rarity
    // refers to) lives in the main mod, which this library module has no visibility into -
    // the requirement is actually evaluated where GearRarity is visible.
    public int min_tier = 0;
    public String min_rarity = "";
    public boolean require_uber = false;

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
}
