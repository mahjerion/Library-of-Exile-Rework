package com.robertx22.library_of_exile.dimension.structure;

import com.robertx22.library_of_exile.dimension.MapDimensionInfo;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Chunks of a map dimension that loaded and still need to be checked for the bedrock hole that
 * generation can leave behind. See {@link MapStructure#repairChunksAround}.
 * <p>
 * This exists because the obvious way to defer that check does not actually defer it.
 * {@code server.execute(task)} only queues the task when {@code scheduleExecutables()} is true,
 * which is {@code runningTask() || !isSameThread()} - so a chunk load event fired on the server
 * thread outside a task runs the repair <i>inline</i>, in the middle of that chunk loading.
 * Placing a room reads blocks, reading blocks can load another chunk, and that chunk's load event
 * starts another repair on top of the first. On the live server that nesting was over half of all
 * time spent in ticks longer than 100ms, with single ticks reaching 3.4 seconds.
 * <p>
 * So the load event only records the position here, and the repairs happen on the level tick, a
 * fixed budget at a time, where nothing is holding a chunk half-loaded underneath them.
 */
public class MapChunkRepairQueue {

    /**
     * How many chunks one level may hand to the structures per tick. The check is cheap for an
     * already carved chunk - {@code isUncarved} returns on its first block read - so this is really
     * a cap on how many genuinely broken chunks can be re-placed in the same tick, which is the
     * expensive case worth spreading out.
     */
    private static final int CHUNKS_PER_TICK = 8;

    /**
     * A backlog this long means chunks are being generated broken faster than they can be repaired,
     * which is a bug somewhere else. Drop the overflow rather than grow without bound - a dropped
     * chunk is retried the next time it loads, and the warning below says it happened.
     */
    private static final int MAX_PENDING_PER_LEVEL = 4096;

    // ConcurrentHashMap because chunk load events are not guaranteed to arrive on the server thread,
    // and the set doubles as dedup for a chunk that loads, unloads and loads again before its turn.
    private static final Map<ResourceKey<Level>, Set<ChunkPos>> PENDING = new ConcurrentHashMap<>();

    private static boolean warnedFull = false;

    public static void enqueue(ServerLevel level, ChunkPos cpos) {
        Set<ChunkPos> pending = PENDING.computeIfAbsent(level.dimension(), k -> ConcurrentHashMap.newKeySet());

        if (pending.size() >= MAX_PENDING_PER_LEVEL) {
            if (!warnedFull) {
                warnedFull = true;
                ExileLog.get().warn("Map chunk repair backlog for " + level.dimension().location()
                        + " hit " + MAX_PENDING_PER_LEVEL + " chunks, so some are being dropped. Chunks are being"
                        + " generated as solid bedrock faster than they can be re-placed, which points at a"
                        + " problem in chunk generation rather than in the repair. Dropped chunks are retried"
                        + " the next time they load.");
            }
            return;
        }
        pending.add(cpos);
    }

    /**
     * Repairs up to {@link #CHUNKS_PER_TICK} of this level's pending chunks. Safe to call for any
     * level - it returns immediately for anything that is not a map dimension or has nothing queued.
     */
    public static void tick(ServerLevel level) {
        Set<ChunkPos> pending = PENDING.get(level.dimension());
        if (pending == null || pending.isEmpty()) {
            return;
        }

        MapDimensionInfo info = MapDimensions.getInfo(level);
        if (info == null) {
            // the dimension stopped being a map, or was never one. nothing will ever repair these.
            pending.clear();
            return;
        }

        // one batch, not one call per chunk: repairChunksAround groups by instance so it pays the
        // LRU lookup and the room ResourceLocation build once per instance instead of once per chunk.
        List<ChunkPos> batch = new ArrayList<>(CHUNKS_PER_TICK);
        Iterator<ChunkPos> it = pending.iterator();
        while (it.hasNext() && batch.size() < CHUNKS_PER_TICK) {
            batch.add(it.next());
            it.remove();
        }

        if (batch.isEmpty()) {
            return;
        }

        try {
            info.structure.repairChunksAround(level, batch);
            for (MapStructure<?> secondary : info.secondaryStructures) {
                secondary.repairChunksAround(level, batch);
            }
        } catch (Exception e) {
            ExileLog.get().error("Failed while repairing map chunks of " + level.dimension().location() + ".", e);
        }
    }

    /**
     * Drops everything queued for a level. Called when a dimension is wiped: those coordinates are
     * about to belong to a different instance, and a queued position from the old one has nothing
     * left to say about it.
     */
    public static void clear(ServerLevel level) {
        Set<ChunkPos> pending = PENDING.remove(level.dimension());
        if (pending != null) {
            pending.clear();
        }
        warnedFull = false;
    }
}
