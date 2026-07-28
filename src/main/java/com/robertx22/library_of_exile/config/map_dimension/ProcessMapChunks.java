package com.robertx22.library_of_exile.config.map_dimension;

import com.robertx22.library_of_exile.components.BlockData;
import com.robertx22.library_of_exile.components.LibChunkCap;
import com.robertx22.library_of_exile.components.LibMapCap;
import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.database.map_data_block.DataBlockText;
import com.robertx22.library_of_exile.database.map_data_block.MapBlockCtx;
import com.robertx22.library_of_exile.database.map_data_block.MapDataBlock;
import com.robertx22.library_of_exile.dimension.MapDimensionInfo;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessMapChunks {

    static List<ChunkPos> getChunksInRadius(Player p, int terrain) {
        var start = p.chunkPosition();
        List<ChunkPos> all = new ArrayList<>();
        all.add(start);

        for (int x = -terrain; x < terrain; x++) {
            for (int z = -terrain; z < terrain; z++) {
                all.add(new ChunkPos(start.x + x, start.z + z));
            }
        }
        return all;
    }

    public static void process(Player p, MapDimensionInfo info, MapDimensionConfig config, ChunkProcessType type) {

        var processChunks = getChunksInRadius(p, config.CHUNK_PROCESS_RADIUS.get());
        var spawnChunks = getChunksInRadius(p, config.CHUNK_SPAWN_RADIUS.get());
        var level = p.level();

        for (ChunkPos cpos : processChunks) {
            if (!level.hasChunk(cpos.x, cpos.z)) {
                continue;
            }
            ChunkAccess c = level.getChunk(cpos.x, cpos.z);

            if (c instanceof LevelChunk chunk) {
                //var chunkdata = Load.chunkData(chunk);
                var cap = chunk.getCapability(LibChunkCap.INSTANCE).orElse(new LibChunkCap(chunk));

                if (!cap.mapGenData.generatedData(info.structure)) {
                    cap.mapGenData.setGeneratedData(info.structure);
                    generateData(level, chunk);
                    ExileEvents.PROCESS_CHUNK_DATA.callEvents(new ExileEvents.OnProcessChunkData(p, info.structure, cpos));
                }
            }
        }
        for (ChunkPos cpos : spawnChunks) {
            if (!level.hasChunk(cpos.x, cpos.z)) {
                continue;
            }
            ChunkAccess c = level.getChunk(cpos.x, cpos.z);

            if (c instanceof LevelChunk chunk) {
                spawnDataFromChunk(level, chunk, type);

            }
        }
    }


    public static void spawnDataFromChunk(Level level, LevelChunk chunk, ChunkProcessType type) {

        CompoundTag data = new CompoundTag();

        // fetch the processor list once per call instead of re-allocating it per data block (getList() allocates a fresh ArrayList)
        List<MapDataBlock> processors = LibDatabase.MapDataBlocks().getList();

        chunk.getCapability(LibChunkCap.INSTANCE).ifPresent(x -> {
            if (!x.mapGenData.mapBlocks.isEmpty()) {
                for (BlockData block : x.mapGenData.mapBlocks.getOrDefault(type, Arrays.asList())) {
                    generateSingleData(level, chunk, type, block.getPos(), data, block.data, processors);
                }
            }
            x.mapGenData.mapBlocks.put(type, new ArrayList<>());
        });

    }

    public static void generateData(Level level, LevelChunk chunk) {

        var nbt = new CompoundTag();

        // fetch the processor list once per chunk instead of re-allocating it for every block entity
        List<MapDataBlock> processors = LibDatabase.MapDataBlocks().getList();

        chunk.getCapability(LibChunkCap.INSTANCE).ifPresent(x -> {
            for (BlockPos tilePos : chunk.getBlockEntitiesPos()) {
                BlockEntity tile = level.getBlockEntity(tilePos);
                // functional command blocks come back empty here, so they are neither recorded into the
                // chunk cap nor removed below - they stay in the world and run like any other command block
                var text = getDataBlockKey(level, tile, processors);

                if (!text.isEmpty()) {

                    ChunkProcessType type = ChunkProcessType.NORMAL;

                    for (MapDataBlock processor : processors) {
                        if (processor.matches(text, tilePos, level, nbt)) {
                            type = processor.process_on;
                            break;
                        }
                    }

                    if (!x.mapGenData.mapBlocks.containsKey(type)) {
                        x.mapGenData.mapBlocks.put(type, new ArrayList<>());
                    }

                    x.mapGenData.mapBlocks.get(type).add(new BlockData(tilePos.asLong(), text));

                    level.removeBlock(tilePos, false);
                }
            }
        });
    }

    public static void generateSingleData(Level level, LevelChunk chunk, ChunkProcessType type, BlockPos tilePos, CompoundTag data, String text, List<MapDataBlock> processors) {

        if (!text.isEmpty()) {

            boolean any = false;

            boolean skip = false;

            var libdata = LibMapCap.getData(level, tilePos);

            var ctx = new MapBlockCtx();

            ctx.libMapData = libdata;

            // only process() a matching processor - non-matching ones previously reached process() only to no-op
            // (process() re-checks matches() internally), so skipping them is behavior-identical and avoids the double-match
            for (MapDataBlock processor : processors) {
                if (processor.matches(text, tilePos, level, data)) {
                    if (processor.process_on != type) {
                        skip = true;
                        break;
                    }
                    boolean did = processor.process(text, tilePos, level, data, ctx);
                    if (did) {
                        any = true;
                    }
                }
            }
            if (!skip) {
                if (any) {
                    // only set to air if the processor didnt turn it into another block
                    if (level.getBlockState(tilePos).getBlock() == Blocks.STRUCTURE_BLOCK || level.getBlockState(tilePos).getBlock() == Blocks.COMMAND_BLOCK) {
                        //level.destroyBlock(tilePos, false);
                        //  level.removeBlockEntity(tilePos);
                        // level.setBlock(tilePos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL); // delete data block
                    }
                } else {
                    level.removeBlock(tilePos, false);
                    boolean oldComplex = text.contains("spawn") && text.contains(";");

                    if (!oldComplex) {
                        // let's not error on these..
                        ExileLog.get().warn("Data block with id: " + text + " matched no processors! " + tilePos.toString());
                    }
                    var info = MapDimensions.getInfo(level);
                    if (info != null) {
                        String id = info.config.DEFAULT_DATA_BLOCK.get();
                        LibDatabase.MapDataBlocks().get(id).process(id, tilePos, level, data, ctx);
                    }
                }

            }
        }
    }

    public static String getDataString(BlockEntity be) {
        // todo first convert to invis data blocks

        if (be instanceof StructureBlockEntity struc) {
            CompoundTag nbt = struc.saveWithoutMetadata();
            return nbt.getString("metadata");
        }
        if (be instanceof CommandBlockEntity cb) {
            return cb.getCommandBlock().getCommand();
        }

        return "";
    }

    /**
     * The data block key of a block, or empty if it isn't a data block at all. Everything that looks
     * up data blocks goes through this rather than the raw {@link #getDataString} readers, so a
     * command block a builder put in a room to actually run its command can't be mistaken for content
     * and eaten by the chunk scan.
     * <p>
     * Only command blocks are classified. A structure block can't execute anything, so treating its
     * metadata as a command would just leave a visible structure block standing in the room.
     */
    public static String getDataBlockKey(Level level, BlockEntity be, List<MapDataBlock> processors) {
        String text = getDataString(be);
        if (be instanceof CommandBlockEntity && isFunctionalCommand(level, text, processors)) {
            return "";
        }
        return text;
    }

    public static String getDataBlockKey(Level level, StructureTemplate.StructureBlockInfo block) {
        CompoundTag nbt = block.nbt();
        if (nbt == null) {
            return "";
        }
        if (nbt.contains("metadata")) { // structure block, never a command
            return nbt.getString("metadata");
        }
        if (nbt.contains("Command")) {
            String text = nbt.getString("Command");
            return isFunctionalCommand(level, text, LibDatabase.MapDataBlocks().getList()) ? "" : text;
        }
        return "";
    }

    /**
     * @param level the dimension the block is in, or null when it isn't known yet (a room read
     *              straight off its template). A null level, or one outside a map dimension, just
     *              means the per-dimension opt-out can't apply.
     */
    private static boolean isFunctionalCommand(Level level, String text, List<MapDataBlock> processors) {
        if (!DataBlockText.isFunctionalCommand(text, processors)) {
            return false;
        }
        if (level == null) {
            return true;
        }
        var info = MapDimensions.getInfo(level);
        return info == null || info.config.ALLOW_FUNCTIONAL_COMMAND_BLOCKS.get();
    }

    // same two conventions as above, but read straight off a structure template's block info, for
    // callers that look at a room before it's placed in the world and so have no block entity yet
    public static String getDataString(StructureTemplate.StructureBlockInfo block) {
        CompoundTag nbt = block.nbt();
        if (nbt == null) {
            return "";
        }
        if (nbt.contains("metadata")) { // structure block
            return nbt.getString("metadata");
        }
        if (nbt.contains("Command")) { // command block
            return nbt.getString("Command");
        }
        return "";
    }
}
