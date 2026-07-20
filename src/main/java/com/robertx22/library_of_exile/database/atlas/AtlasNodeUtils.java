package com.robertx22.library_of_exile.database.atlas;

import com.robertx22.library_of_exile.database.init.LibDatabase;

import java.util.Optional;
import java.util.Set;

public class AtlasNodeUtils {

    public static Optional<AtlasNode> byDungeon(String dungeonGuid) {
        if (dungeonGuid == null || dungeonGuid.isEmpty()) {
            return Optional.empty();
        }
        return LibDatabase.AtlasNodes()
                .getList()
                .stream()
                .filter(x -> dungeonGuid.equals(x.dungeon))
                .findFirst();
    }

    // A dungeon with no Atlas node stays ungated (backward compat for non-atlas dungeons).
    public static boolean isDungeonUnlocked(Set<String> unlockedNodeIds, String dungeonGuid) {
        Optional<AtlasNode> node = byDungeon(dungeonGuid);
        if (node.isEmpty()) {
            return true;
        }
        return unlockedNodeIds.contains(node.get().id);
    }
}
