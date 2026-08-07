package com.robertx22.library_of_exile.dimension.structure;

import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Boot time check that a prebuilt map (an arena, a reward room) actually ships every room it claims.
 * <p>
 * {@link SimplePrebuiltMapData} describes a square {@code (size+1) x (size+1)} grid of one chunk
 * rooms named {@code x_z}. Any of those the datapack doesn't ship is not an error at generation
 * time - the chunk simply stays the solid bedrock it was born as, silently and permanently. That is
 * indistinguishable, to a player, from the intermittent generation bug this was written for, so it
 * is worth ruling out once at startup instead of one player report at a time.
 */
public class PrebuiltMapCheck {

    /**
     * @param label how to name this map in the log, e.g. "boss arena 'sandstone'"
     * @return the problems found, empty if the map is complete
     */
    public static List<String> problems(MinecraftServer server, String label, SimplePrebuiltMapData map) {
        List<String> problems = new ArrayList<>();

        if (map == null) {
            problems.add(label + " has no structure data at all");
            return problems;
        }

        var man = server.getStructureManager();
        map.resolveFootprint(man);

        if (!map.hasResolvedFootprint()) {
            problems.add(label + " ships no rooms at all under '" + map.rooms_folder + "'");
            return problems;
        }

        // expectedRooms is the resolved rectangle, so a map that is legitimately rectangular - the
        // house style for uber arenas is (size+1) wide by size deep - reports nothing, and only a
        // hole INSIDE the rectangle gets flagged.
        //
        // the trade-off, taken deliberately: deleting a square map's entire last row now silently
        // yields a smaller rectangle instead of a warning. that is the right call - it generates as a
        // complete rectangle either way, and is indistinguishable from the intentional case without
        // reading the author's mind. holes, which are the actionable mistake, are still caught.
        for (ResourceLocation room : map.expectedRooms()) {
            var opt = man.get(room);
            if (opt.isEmpty() || opt.get() == null) {
                problems.add(label + " has a hole at " + room + " (its rooms span 0.."
                        + map.maxRoomX() + " by 0.." + map.maxRoomZ() + ")");
                continue;
            }
            var size = opt.get().getSize();
            if (size.getX() > 16 || size.getZ() > 16) {
                problems.add(label + " room " + room + " is bigger than one chunk " + size);
            }
        }
        return problems;
    }

    /**
     * Runs the check over several maps and logs one block, so a pack with a few incomplete arenas
     * reads as a list instead of scattered lines. Logs nothing when everything is complete.
     */
    public static void reportAll(MinecraftServer server, String what, List<Entry> entries) {
        List<String> all = new ArrayList<>();
        for (Entry e : entries) {
            all.addAll(problems(server, e.label, e.map));
        }
        if (all.isEmpty()) {
            return;
        }
        ExileLog.get().warn("Incomplete " + what + " found. A hole leaves that chunk SOLID BEDROCK inside"
                + " the room, permanently - ship the missing .nbt. (A map that is simply rectangular is"
                + " fine and is not listed here.)");
        all.forEach(x -> ExileLog.get().warn("  - " + x));
    }

    public static class Entry {
        public final String label;
        public final SimplePrebuiltMapData map;

        public Entry(String label, SimplePrebuiltMapData map) {
            this.label = label;
            this.map = map;
        }
    }
}
