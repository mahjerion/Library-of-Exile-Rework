package com.robertx22.library_of_exile.dimension.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

import java.util.Comparator;

/**
 * Asks the chunk pipeline to bring a teleport destination up to {@code FULL} in the background,
 * and answers whether it has finished - without ever blocking the server thread.
 * <p>
 * This exists because the obvious way to make sure a destination is ready is
 * {@code level.getChunk(pos)}, which is a synchronous, blocking, generate-if-missing load on the
 * server thread. It does not prevent the freeze it is usually written to prevent, it just moves
 * it: on a 25 player server that call plus Forge's {@code Entity.setPosRaw} patch (which blocks
 * on the player's own chunk every tick, for every player, whenever it is not yet FULL) accounted
 * for 44.8% of all time spent in ticks over 100ms.
 * <p>
 * Vanilla already has both halves of the right answer, it just never puts them together:
 * {@code ServerPlayer.teleportTo} adds a POST_TELEPORT ticket but moves the player in the same
 * call without waiting (and that ticket expires after 5 ticks, long before a fresh map instance
 * generates), while {@code Entity.teleportToWithTicket} waits but does it by blocking. Ticket it,
 * wait across ticks, then teleport.
 */
public class MapChunkPreloader {

    /**
     * Chunks either side of the destination to bring up with it. The player lands in the middle
     * and immediately needs the ring around them; 2 (a 5x5) covers the arrival without turning
     * every map entry into a bulk generation job - a whole dungeon footprint is 12-80 chunks, and
     * 25 players entering at once would queue thousands of them against ~3 worker threads.
     */
    public static final int RADIUS = 2;

    /**
     * The lifespan is a safety net, and it is the ONLY cleanup this class does - see the class
     * comment on why nothing calls a release explicitly. 200 ticks (10s) comfortably outlives the
     * wait, after which vanilla's own ticket purge drops these and the arriving player's PLAYER
     * ticket is what keeps the chunks alive.
     */
    private static final TicketType<ChunkPos> PRELOAD =
            TicketType.create("exile_preload", Comparator.comparingLong(ChunkPos::toLong), 200);

    /**
     * Starts the load. Cheap and idempotent - re-adding a ticket for a chunk that already has one
     * just refreshes it, and a chunk that is already FULL is left alone.
     * <p>
     * Deliberately one distance-0 ticket per chunk rather than a single radius-{@code radius}
     * region ticket. {@code addRegionTicket} builds its ticket at
     * {@code ChunkLevel.byStatus(FullChunkStatus.FULL) - distance}, so distance 0 is level 33 -
     * loaded and fully generated, but {@code ChunkLevel.isEntityTicking(33)} and
     * {@code isBlockTicking(33)} are both false.
     * <p>
     * <b>That "does not tick" property is load bearing, not an optimisation.</b> These chunks are
     * generated while the instance is still empty, and a room template can carry entities
     * ({@code MapGenerationUTIL.spawnStructure} places with {@code setIgnoreEntities(false)}). In
     * the harvest and obelisk dimensions a mob takes its level from the nearest player *in the
     * same instance*, falling back to the dimension's {@code min_lvl} when there is none - so a
     * mob that ticked here, before the player arrived, would level itself off an empty instance.
     * A single region ticket of radius 2 would put the centre chunk at level 31 = ENTITY_TICKING
     * and do exactly that. Keep it one ticket per chunk at distance 0.
     */
    public static void request(ServerLevel level, ChunkPos center, int radius) {
        for (int x = center.x - radius; x <= center.x + radius; x++) {
            for (int z = center.z - radius; z <= center.z + radius; z++) {
                ChunkPos cpos = new ChunkPos(x, z);
                level.getChunkSource().addRegionTicket(PRELOAD, cpos, 0, cpos);
            }
        }
    }

    /**
     * Whether every chunk of the square is loaded to FULL. Non-blocking:
     * {@code ServerChunkCache.hasChunk} only asks whether the chunk holder has reached that level
     * and never starts or waits on work, which is the same test {@code repairChunksAround} uses
     * before it will touch a chunk.
     */
    public static boolean isReady(ServerLevel level, ChunkPos center, int radius) {
        for (int x = center.x - radius; x <= center.x + radius; x++) {
            for (int z = center.z - radius; z <= center.z + radius; z++) {
                if (!level.getChunkSource().hasChunk(x, z)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static ChunkPos centerOf(BlockPos pos) {
        return new ChunkPos(pos);
    }
}
