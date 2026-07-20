package com.robertx22.library_of_exile.database.atlas;

import com.robertx22.library_of_exile.database.init.LibDatabase;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AtlasNodeUtils {

    // a dungeon can be referenced by more than one node (e.g. a plain entry node plus a later,
    // harder-requirement repeat of the same dungeon) - callers that care about a run's specific
    // requirements should filter/inspect this list themselves.
    public static List<AtlasNode> byDungeon(String dungeonGuid) {
        if (dungeonGuid == null || dungeonGuid.isEmpty()) {
            return List.of();
        }
        return LibDatabase.AtlasNodes()
                .getList()
                .stream()
                .filter(x -> dungeonGuid.equals(x.dungeon))
                .collect(Collectors.toList());
    }

    // A dungeon with no Atlas node stays ungated (backward compat for non-atlas dungeons).
    // Unlocked if ANY of its nodes is unlocked, so a harder repeat node further out doesn't
    // re-gate maps that are already obtainable via the dungeon's base node.
    public static boolean isDungeonUnlocked(Set<String> unlockedNodeIds, String dungeonGuid) {
        List<AtlasNode> nodes = byDungeon(dungeonGuid);
        if (nodes.isEmpty()) {
            return true;
        }
        return nodes.stream().anyMatch(node -> unlockedNodeIds.contains(node.id));
    }
}
