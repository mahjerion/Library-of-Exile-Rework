package com.robertx22.library_of_exile.dimension;

import com.robertx22.library_of_exile.components.LibMapCap;
import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class MapDimensions {

    // this way I can keep track of what dimensions are maps
    // keyed by ResourceLocation, not by its toString(): isMap runs from the Level#getWorldBorder mixin,
    // one of the hottest methods in the game, and building the "namespace:path" string on every call
    // allocated ~700MB per 2 minutes of server time on a 20 player server. ResourceLocation already
    // hashes and compares by namespace+path, so the key change is behaviour-neutral.
    private static HashMap<ResourceLocation, MapDimensionInfo> map = new HashMap<>();


    public static boolean isMap(ResourceLocation id) {
        return map.containsKey(id);
    }

    public static boolean isMap(Level world) {
        var dim = world.dimensionTypeId().location();
        boolean is = isMap(dim);
        return is;
    }

    public static MapDimensionInfo getInfo(ResourceLocation id) {
        return map.get(id);
    }

    // the map is keyed by dimension id, but a structure's guid is not a dimension id - dungeon_realm's
    // is "dungeon", and its arena/reward rooms are "boss_arena"/"reward_room". looking a structure up by
    // guid therefore always missed, which silently killed every map connection created from a dungeon and
    // left callers unable to resolve which dimension a structure belongs to. match on the structure
    // itself instead, primary or secondary.
    public static MapDimensionInfo getInfo(MapStructure s) {
        if (s == null) {
            return null;
        }
        for (MapDimensionInfo info : map.values()) {
            if (info.hasStructure(s)) {
                return info;
            }
        }
        return null;
    }

    public static MapDimensionInfo getInfo(Level world) {
        return getInfo(world.dimensionTypeId().location());
    }

    public static void register(MapDimensionInfo info) {
        map.put(info.dimensionId, info);

        // LibMapData (relic stats) is keyed only by instance coordinates, with no dimension in the key, so
        // it can only be cleared wholesale. That is safe here because the primary content dimension is its
        // only writer - deliberately NOT registered for side content, since a wipe of one league can be
        // run by itself (the wipe_world_data command) while another league's maps are still being played.
        if (info.contentType.canSpawnSideContent) {
            info.addOnWipeListener(server -> LibMapCap.get(server.overworld()).data.clearAll());
        }
    }
}
