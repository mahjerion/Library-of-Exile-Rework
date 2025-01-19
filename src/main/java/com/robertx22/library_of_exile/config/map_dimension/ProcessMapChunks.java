package com.robertx22.library_of_exile.config.map_dimension;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.database.map_data_block.MapDataBlock;
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

    public static void process(Player p, MapDimensionConfig config) {

        var chunks = getChunksInRadius(p, config);
        var level = p.level();

        for (ChunkPos cpos : chunks) {
            if (!level.hasChunk(cpos.x, cpos.z)) {
                continue;
            }
            ChunkAccess c = level.getChunk(cpos.x, cpos.z);

            // todo do i even need this? Is grabbing tile entities once a second laggy enough to warrant making sure its only done once per chunk?
            if (c instanceof LevelChunk chunk) {

                //var chunkdata = Load.chunkData(chunk);

                if (true) {
                    generateData(level, chunk);
                    //chunkdata.generatedMobs = true;
                }
            }


        }

    }

    public static void generateData(Level level, LevelChunk chunk) {

        CompoundTag data = new CompoundTag();

        for (BlockPos tilePos : chunk.getBlockEntitiesPos()) {

            BlockEntity tile = level.getBlockEntity(tilePos);
            var text = getDataString(tile);
            if (!text.isEmpty()) {

                boolean any = false;

                for (MapDataBlock processor : LibDatabase.MapDataBlocks().getList()) {
                    boolean did = processor.process(text, tilePos, level, data);
                    if (did) {
                        any = true;
                    }
                }

                if (any) {
                    // only set to air if the processor didnt turn it into another block
                    if (level.getBlockState(tilePos).getBlock() == Blocks.STRUCTURE_BLOCK || level.getBlockState(tilePos).getBlock() == Blocks.COMMAND_BLOCK) {
                        level.removeBlockEntity(tilePos);
                        level.setBlock(tilePos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL); // delete data block
                    }

                } else {
                    level.setBlock(tilePos, Blocks.BEDROCK.defaultBlockState(), Block.UPDATE_ALL);
                    ExileLog.get().warn("Data block with tag: " + text + " matched no processors! " + tilePos.toString());
                    //logRoomForPos(level, tilePos);
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
