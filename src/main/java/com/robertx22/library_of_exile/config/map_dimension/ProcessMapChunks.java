package com.robertx22.library_of_exile.config.map_dimension;

import com.robertx22.library_of_exile.components.LibChunkCap;
import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.database.map_data_block.MapDataBlock;
import com.robertx22.library_of_exile.dimension.MapDimensionInfo;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;

public class ProcessMapChunks {

    static List<ChunkPos> getChunksInRadius(Player p, MapDimensionConfig config) {
        var start = p.chunkPosition();
        List<ChunkPos> all = new ArrayList<>();
        all.add(start);

        int terrain = config.CHUNK_PROCESS_RADIUS.get();

        for (int x = -terrain; x < terrain; x++) {
            for (int z = -terrain; z < terrain; z++) {
                all.add(new ChunkPos(start.x + x, start.z + z));
            }
        }
        return all;
    }

    public static void process(Player p, MapDimensionInfo info, MapDimensionConfig config, ChunkProcessType type) {

        var chunks = getChunksInRadius(p, config);
        var level = p.level();

        for (ChunkPos cpos : chunks) {
            if (!level.hasChunk(cpos.x, cpos.z)) {
                continue;
            }
            ChunkAccess c = level.getChunk(cpos.x, cpos.z);

            if (c instanceof LevelChunk chunk) {
                //var chunkdata = Load.chunkData(chunk);
                var cap = chunk.getCapability(LibChunkCap.INSTANCE).orElse(new LibChunkCap(chunk));

                if (!cap.mapGenData.generatedData(info.structure)) {
                    cap.mapGenData.setGeneratedData(info.structure);
                    generateData(level, chunk, type);
                    ExileEvents.PROCESS_CHUNK_DATA.callEvents(new ExileEvents.OnProcessChunkData(p, info.structure, cpos));
                }
            }
        }

    }

    public static void generateData(Level level, LevelChunk chunk, ChunkProcessType type) {

        CompoundTag data = new CompoundTag();

        for (BlockPos tilePos : chunk.getBlockEntitiesPos()) {

            BlockEntity tile = level.getBlockEntity(tilePos);
            var text = getDataString(tile);
            if (!text.isEmpty()) {

                boolean any = false;

                boolean skip = false;

                for (MapDataBlock processor : LibDatabase.MapDataBlocks().getList()) {
                    if (processor.matches(text, tilePos, level, data)) {
                        if (processor.process_on != type) {
                            skip = true;
                            break;
                        }
                    }
                    boolean did = processor.process(text, tilePos, level, data);
                    if (did) {
                        any = true;
                    }
                }
                if (!skip) {
                    if (any) {
                        // only set to air if the processor didnt turn it into another block
                        if (level.getBlockState(tilePos).getBlock() == Blocks.STRUCTURE_BLOCK || level.getBlockState(tilePos).getBlock() == Blocks.COMMAND_BLOCK) {
                            level.removeBlockEntity(tilePos);
                            level.setBlock(tilePos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL); // delete data block
                        }
                    } else {
                        level.setBlock(tilePos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        boolean oldComplex = text.contains("spawn") && text.contains(";");

                        if (!oldComplex) {
                            // let's not error on these..
                            ExileLog.get().warn("Data block with id: " + text + " matched no processors! " + tilePos.toString());
                        }
                        var config = MapDimensionConfig.get(level.dimensionTypeId().location());
                        if (config != null) {
                            LibDatabase.MapDataBlocks().get(config.DEFAULT_DATA_BLOCK.get()).process(text, tilePos, level, data);
                        }
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
}
