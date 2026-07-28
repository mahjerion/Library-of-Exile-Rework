package com.robertx22.library_of_exile.database.map_data_block;

import com.robertx22.library_of_exile.database.init.LibDatabase;

import java.util.List;

/**
 * Decides whether the command of a command block inside a map structure is a data block key or an
 * actual Minecraft command meant to run.
 * <p>
 * Rooms are authored by putting command blocks in structure NBT and typing a registry id as the
 * command, so the two uses share one field and have to be told apart by the text alone:
 * <pre>
 * 1. matches a registered MapDataBlock id/alias?  -> data block (registered content always wins)
 * 2. legacy "spawn;stray;4" form?                 -> data block
 * 3. starts with '/' or contains whitespace?      -> real command, leave the block alone
 * 4. otherwise (unknown single token)             -> data block, so it still hits DEFAULT_DATA_BLOCK
 * </pre>
 * Checking the registry first is what keeps this safe for structures that already exist: every
 * registered id and alias is a single token, and a datapack-added id is matched outright instead of
 * being guessed at. Step 4 keeping unknown single tokens on the data block path is what preserves the
 * "old/typo'd id turns into a mob pack" fallback - only text that actually looks like a command is
 * taken away from it.
 */
public class DataBlockText {

    private DataBlockText() {
    }

    public static boolean isFunctionalCommand(String text) {
        return isFunctionalCommand(text, LibDatabase.MapDataBlocks().getList());
    }

    /**
     * @param processors the registered data blocks, passed in so callers already holding the list
     *                   (getList() allocates a fresh ArrayList) don't refetch it per block.
     */
    public static boolean isFunctionalCommand(String text, List<MapDataBlock> processors) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (MapDataBlock processor : processors) {
            if (processor.matches(text, null, null, null)) {
                return false;
            }
        }
        if (text.contains("spawn") && text.contains(";")) {
            // old "spawn;stray;4" data blocks, which no longer parse but still belong to the fallback
            return false;
        }
        return text.startsWith("/") || hasWhitespace(text);
    }

    private static boolean hasWhitespace(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
