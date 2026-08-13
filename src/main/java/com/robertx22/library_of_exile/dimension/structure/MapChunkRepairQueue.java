package com.robertx22.library_of_exile.dimension.structure;

import com.robertx22.library_of_exile.dimension.MapDimensionInfo;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashSet;
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

    /**
     * How long to wait before re-offering a chunk a structure deferred. A deferred chunk is waiting on
     * something outside this class - map data being written for a brand new instance - and one second
     * is both fast enough that a player never sees the bedrock and slow enough that a stuck instance
     * costs nothing measurable. It is also the cadence the old per-player-per-second repair scan ran
     * at, which is the behaviour this restores.
     */
    private static final int RETRY_INTERVAL_TICKS = 20;

    /**
     * How many times one chunk may be deferred before it is abandoned. At the interval above this is
     * five minutes, which is far longer than the window it is actually covering (map data is written in
     * the same tick the instance is created, and the race is against chunk generation, not against a
     * player). Giving up is safe: a player entering that instance later loads its chunks, and a chunk
     * load queues it again from scratch.
     */
    private static final int MAX_RETRIES = 300;

    // ConcurrentHashMap because chunk load events are not guaranteed to arrive on the server thread,
    // and the set doubles as dedup for a chunk that loads, unloads and loads again before its turn.
    private static final Map<ResourceKey<Level>, Set<ChunkPos>> PENDING = new ConcurrentHashMap<>();

    // chunks a structure refused to carve for now, and how many times it has refused. Held aside rather
    // than left in PENDING so a stuck instance cannot occupy the whole per tick budget and starve chunks
    // that could actually be repaired.
    private static final Map<ResourceKey<Level>, Map<ChunkPos, Integer>> DEFERRED = new ConcurrentHashMap<>();

    // game time at which this level's deferred chunks go back into PENDING
    private static final Map<ResourceKey<Level>, Long> NEXT_RETRY = new ConcurrentHashMap<>();

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
        promoteDeferred(level);

        Set<ChunkPos> pending = PENDING.get(level.dimension());
        if (pending == null || pending.isEmpty()) {
            return;
        }

        MapDimensionInfo info = MapDimensions.getInfo(level);
        if (info == null) {
            // the dimension stopped being a map, or was never one. nothing will ever repair these.
            pending.clear();
            DEFERRED.remove(level.dimension());
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

        // one set, shared by every structure: a chunk both the dungeon and an arena want to retry is
        // still one chunk to re-offer
        Set<ChunkPos> deferred = new HashSet<>();

        try {
            info.structure.repairChunksAround(level, batch, deferred);
            for (MapStructure<?> secondary : info.secondaryStructures) {
                secondary.repairChunksAround(level, batch, deferred);
            }
        } catch (Exception e) {
            ExileLog.get().error("Failed while repairing map chunks of " + level.dimension().location() + ".", e);
        }

        // anything in this batch nobody asked for again is done with - repaired, already carved, or
        // never this structure's to carve. Without this, a chunk that was deferred once would be
        // re-offered every second for the rest of the world's life.
        Map<ChunkPos, Integer> waiting = DEFERRED.get(level.dimension());
        if (waiting != null && !waiting.isEmpty()) {
            for (ChunkPos cpos : batch) {
                if (!deferred.contains(cpos)) {
                    waiting.remove(cpos);
                }
            }
        }

        if (!deferred.isEmpty()) {
            defer(level, deferred);
        }
    }

    /**
     * Holds chunks a structure asked to be re-offered, and counts how many times each has asked.
     * <p>
     * Anything that runs out of attempts is dropped with one aggregated log line. Per chunk lines would
     * be 25+ for a single stuck instance, which is exactly the kind of flood that makes a real signal
     * unreadable.
     */
    private static void defer(ServerLevel level, Set<ChunkPos> chunks) {
        Map<ChunkPos, Integer> waiting = DEFERRED.computeIfAbsent(level.dimension(), k -> new ConcurrentHashMap<>());

        int gaveUp = 0;
        ChunkPos firstGivenUp = null;

        for (ChunkPos cpos : chunks) {
            int attempts = waiting.getOrDefault(cpos, 0) + 1;
            if (attempts > MAX_RETRIES) {
                waiting.remove(cpos);
                if (firstGivenUp == null) {
                    firstGivenUp = cpos;
                }
                gaveUp++;
                continue;
            }
            waiting.put(cpos, attempts);
        }

        NEXT_RETRY.putIfAbsent(level.dimension(), level.getGameTime() + RETRY_INTERVAL_TICKS);

        if (gaveUp > 0) {
            ExileLog.get().warn("Gave up re-carving " + gaveUp + " chunk(s) of "
                    + level.dimension().location() + " (first at " + firstGivenUp + ") after "
                    + MAX_RETRIES + " attempts over " + (MAX_RETRIES * RETRY_INTERVAL_TICKS / 20)
                    + "s. Their instance's map data never became readable, so they stay solid bedrock"
                    + " until something loads those chunks again. That is a bug in whatever was supposed"
                    + " to write that map data, not in the repair.");
        }
    }

    /**
     * Moves this level's deferred chunks back into {@link #PENDING} once their wait is up. They go
     * through {@link #enqueue} so they stay subject to the same overflow cap as everything else.
     */
    private static void promoteDeferred(ServerLevel level) {
        Map<ChunkPos, Integer> waiting = DEFERRED.get(level.dimension());
        if (waiting == null || waiting.isEmpty()) {
            return;
        }

        Long due = NEXT_RETRY.get(level.dimension());
        if (due != null && level.getGameTime() < due) {
            return;
        }
        NEXT_RETRY.put(level.dimension(), level.getGameTime() + RETRY_INTERVAL_TICKS);

        // the attempt counts stay - they are cleared per chunk by the next defer() that doesn't see it
        // again, and wholesale by clear(). Re-offering is what resets the cycle, not forgetting.
        for (ChunkPos cpos : waiting.keySet()) {
            enqueue(level, cpos);
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
        // the retry state has to go with it, and for the same reason: a deferred position is a promise
        // to re-carve a specific instance, and after a wipe those coordinates belong to a different one
        Map<ChunkPos, Integer> waiting = DEFERRED.remove(level.dimension());
        if (waiting != null) {
            waiting.clear();
        }
        NEXT_RETRY.remove(level.dimension());
        warnedFull = false;
    }
}
