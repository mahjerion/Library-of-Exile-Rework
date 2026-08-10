package com.robertx22.library_of_exile.dimension.structure;

import com.robertx22.library_of_exile.dimension.MapGenerationUTIL;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class MapStructure<Map> {

    public abstract String guid();

    public abstract Map getMap(ChunkPos start);

    public abstract int getSpawnHeight();

    public final int getStructureHeight() {
        return 48;
    }

    public boolean isInside(ServerLevel level, BlockPos pos) {
        return pos.getY() >= getSpawnHeight() && pos.getY() <= (getSpawnHeight() + getStructureHeight());
    }

    protected abstract ChunkPos INTERNALgetStartChunkPos(ChunkPos cp);

    // blocks to shift the spawn from the start chunk's center to the actual room's center, in both
    // X and Z. 0 for single-chunk rooms (and every prebuilt map); dungeons with rooms bigger than one
    // chunk override this so the player lands in the middle of the room instead of a corner quadrant.
    public int getSpawnCenterBlockOffset(ChunkPos start) {
        return 0;
    }

    public Random createRandom(Level level, ChunkPos cp) {
        return MapGenerationUTIL.createRandom(level, getStartChunkPos(cp));
    }

    public ChunkPos getStartChunkPos(ChunkPos cp) {
        var start = INTERNALgetStartChunkPos(cp);
        var start2 = INTERNALgetStartChunkPos(start);

        if (!start.equals(start2)) {
            // throw new RuntimeException(start.toString() + " and " + start2.toString() + " are different, meaning the getStartChunkPos method is failing to produce consistent results");
        }
        return start;
    }

    public ChunkPos getRelativeChunkPosFromStart(ChunkPos pos) {
        ChunkPos start = getStartChunkPos(pos);
        ChunkPos relative = new ChunkPos(pos.x - start.x, pos.z - start.z);
        return relative;
    }

    public ChunkPos getStartChunkPos(BlockPos pos) {
        var start = getStartChunkPos(new ChunkPos(pos));
        return start;
    }

    public List<Player> getAllPlayersInMap(Level world, BlockPos pos) {
        var start = getStartChunkPos(pos);
        return world.players().stream().filter(x -> {
            return getStartChunkPos(x.blockPosition()).equals(start);
        }).collect(Collectors.toList());
    }

    public abstract boolean generateInChunk(ServerLevelAccessor level, StructureTemplateManager man, ChunkPos cpos);

    // columns sampled to decide whether a chunk was ever carved. the chunk's own centre plus the four
    // quadrant centres - a room a player can stand in has air somewhere in at least one of them.
    private static final int[][] PROBE_COLUMNS = {{8, 8}, {4, 4}, {12, 4}, {4, 12}, {12, 12}};

    /**
     * True only when every sampled block in the structure's height band is bedrock, which is exactly
     * the state {@code fillFromNoise} leaves a chunk in before anything carves it.
     * <p>
     * The all-or-nothing test is the point: it is what stops a repair from overwriting a map players
     * have already fought through and dug into. It is also why this is cheap - for a carved chunk the
     * very first probe is the room's floor, so it returns after one block read.
     */
    protected boolean isUncarved(ServerLevel level, ChunkPos cpos) {
        var chunk = level.getChunk(cpos.x, cpos.z);

        int from = getSpawnHeight();
        int to = getSpawnHeight() + getStructureHeight();

        for (int[] col : PROBE_COLUMNS) {
            for (int y = from; y < to; y++) {
                if (!chunk.getBlockState(cpos.getBlockAt(col[0], y, col[1])).is(Blocks.BEDROCK)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Re-carves any of {@code chunks} this structure left as solid bedrock, and returns how many it
     * repaired.
     * <p>
     * A map chunk is born as solid bedrock and is offered to the structures exactly once, when
     * {@code buildSurface} runs - Minecraft never calls it again on a chunk it has already made. So
     * anything that skipped or refused that carve leaves permanent bedrock, and this is the only pass
     * that ever sees a player standing next to one. Callers must only pass chunks that are already
     * loaded; this must never be the thing that forces a chunk to generate.
     */
    public int repairChunksAround(ServerLevel level, Collection<ChunkPos> chunks) {
        return 0;
    }

    // chunks whose repair was attempted and failed - a missing or oversized template, so there is
    // nothing that CAN be placed there.
    //
    // Without this they are retried once a second forever, and each retry pays isUncarved's full
    // 5 columns x 48 heights scan, because the all-bedrock answer is only reached after checking every
    // one of them. The missing template warning is deduplicated, so that cost would be completely
    // silent. Bounded, because a broken pack must not be able to grow it without limit.
    private static final int MAX_REMEMBERED_FAILURES = 4096;
    private static final Set<String> FAILED_REPAIRS = ConcurrentHashMap.newKeySet();

    /**
     * Forget which chunks failed to repair, so a pack that has since been fixed recovers without a
     * restart. Called from the dimension wipe and from datapack reload.
     */
    public static void forgetRepairFailures() {
        FAILED_REPAIRS.clear();
    }

    protected boolean repairFailedBefore(ChunkPos cpos) {
        return FAILED_REPAIRS.contains(failureKey(cpos));
    }

    protected void rememberRepairFailure(ChunkPos cpos) {
        // an unbounded set here would be a slow leak on a server that never restarts. dropping the
        // record just means we retry that chunk later, which is the harmless direction.
        if (FAILED_REPAIRS.size() < MAX_REMEMBERED_FAILURES) {
            FAILED_REPAIRS.add(failureKey(cpos));
        }
    }

    // scoped per structure: the same chunk is offered to the dungeon AND to every secondary
    // structure, at different height bands, and one of them failing says nothing about the others.
    private String failureKey(ChunkPos cpos) {
        return guid() + "@" + cpos.x + "_" + cpos.z;
    }

}
