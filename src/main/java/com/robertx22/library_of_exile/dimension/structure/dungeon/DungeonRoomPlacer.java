package com.robertx22.library_of_exile.dimension.structure.dungeon;

import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DungeonRoomPlacer {

    // rooms are placed once per chunk they cover, so a size warning would otherwise repeat every chunk
    private static final Set<ResourceLocation> WARNED_ABOUT_SIZE = ConcurrentHashMap.newKeySet();

    public static boolean generatePiece(LevelAccessor world, BlockPos position, RandomSource random, Rotation rota, ResourceLocation id) {
        return generatePiece(world, position, random, rota, id, null, true, 16, 0);
    }

    /**
     * @param clip                 if set, only blocks/entities inside this box are placed. used to place a room
     *                             that spans several chunks one chunk at a time, since worldgen only lets us
     *                             write to the chunk currently being generated.
     * @param fireDataBlockEvents  data blocks must only be announced once per room, not once per chunk it covers.
     * @param expectedSize         the dungeon's room size in blocks, every room must fit within it.
     * @param maxHeight            the structure height the map dimension considers "inside", 0 to skip the check.
     */
    public static boolean generatePiece(LevelAccessor world, BlockPos position, RandomSource random, Rotation rota, ResourceLocation id,
                                        @Nullable BoundingBox clip, boolean fireDataBlockEvents, int expectedSize, int maxHeight) {

        var opt = world.getServer().getStructureManager().get(id);
        if (opt.isEmpty()) {
            ExileLog.get().warn("FATAL ERROR: Structure does not exist (" + id + ")");
            return false;
        }
        var template = opt.get();

        if (template.getSize().getX() > expectedSize || template.getSize().getZ() > expectedSize) {
            ExileLog.get().warn("FATAL ERROR: Structure is bigger than possible (" + id + ") " + template.getSize().toString() + " max is " + expectedSize);
            return false;
        }
        if (maxHeight > 0 && template.getSize().getY() > maxHeight) {
            // taller than what the dimension treats as inside its map, so isInside(...) would mis-report it
            ExileLog.get().warn("FATAL ERROR: Structure is taller than the map allows (" + id + ") " + template.getSize().toString() + " max height is " + maxHeight);
            return false;
        }
        warnAboutOddSize(id, template, expectedSize);

        StructurePlaceSettings settings = new StructurePlaceSettings().setMirror(Mirror.NONE)
                .setRotation(rota)
                .setIgnoreEntities(false);

        if (fireDataBlockEvents) {
            List<StructureTemplate.StructureBlockInfo> commandBlocks = template.filterBlocks(BlockPos.ZERO, new StructurePlaceSettings(), Blocks.COMMAND_BLOCK, true);
            List<StructureTemplate.StructureBlockInfo> structureBlocks = template.filterBlocks(BlockPos.ZERO, new StructurePlaceSettings(), Blocks.STRUCTURE_BLOCK, true);

            final BlockPos finalPosition = position;

            commandBlocks
            .forEach((block) -> {
                BlockPos worldPos = finalPosition.offset(block.pos());

                var event = new ExileEvents.DungeonDataBlockPlaced(world, worldPos, block, id);
                ExileEvents.DUNGEON_DATA_BLOCK_PLACED.callEvents(event);
            });

            structureBlocks
            .forEach((block) -> {
                BlockPos worldPos = finalPosition.offset(block.pos());

                var event = new ExileEvents.DungeonDataBlockPlaced(world, worldPos, block, id);
                ExileEvents.DUNGEON_DATA_BLOCK_PLACED.callEvents(event);
            });
        }

        settings.setBoundingBox(clip);

        // next if the structure is to be rotated then it must also be offset, because rotating a structure also moves it
        if (rota == Rotation.COUNTERCLOCKWISE_90) {
            // west: rotate CCW and push +Z
            settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
            position = position.offset(0, 0, template.getSize().getZ() - 1);
        } else if (rota == Rotation.CLOCKWISE_90) {
            // east rotate CW and push +X
            settings.setRotation(Rotation.CLOCKWISE_90);
            position = position.offset(template.getSize().getX() - 1, 0, 0);
        } else if (rota == Rotation.CLOCKWISE_180) {
            // south: rotate 180 and push both +X and +Z
            settings.setRotation(Rotation.CLOCKWISE_180);
            position = position.offset(template.getSize().getX() - 1, 0, template.getSize().getZ() - 1);
        } else //if (nextRoom.rotation == Rotation.NONE)
        {                // north: no rotation
            settings.setRotation(Rotation.NONE);
        }


        // Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE is experimental thing that should reduce updatenighbor block lag in map generation
        var done = template.placeInWorld((ServerLevelAccessor) world, position, position, settings, random, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);

        return done;
    }

    private static void warnAboutOddSize(ResourceLocation id, StructureTemplate template, int expectedSize) {
        if (template.getSize().getX() == expectedSize && template.getSize().getZ() == expectedSize) {
            return;
        }
        if (!WARNED_ABOUT_SIZE.add(id)) {
            return;
        }
        ExileLog.get().warn("Dungeon room (" + id + ") is " + template.getSize().toString()
                + " but every room of its dungeon must be " + expectedSize + " wide. It will generate with gaps.");
    }

    public static boolean generateStructure(MapStructure struc, DungeonBuilder builder, LevelAccessor world, ChunkPos cpos) {

        if (struc instanceof DungeonStructure dungeonStruc) {
            ChunkPos start = dungeonStruc.getStartChunkPos(cpos);
            // shared cache/build path with the map_bug report, so both resolve the identical layout
            builder.builtDungeon = dungeonStruc.getBuiltDungeon(start);
        } else {
            builder.build();
        }

        int roomChunks = builder.getRoomChunks();

        var placement = builder.builtDungeon.getPlacementForChunk(struc, cpos, roomChunks);
        if (placement == null) {
            return false;
        }

        int y = struc.getSpawnHeight();
        int maxHeight = struc.getStructureHeight();

        if (placement.room.room.isBarrier) {
            // the barrier is a chunk sized filler, so for bigger rooms it's simply tiled over every sub chunk
            return generatePiece(world, cpos.getBlockAt(0, y, 0), world.getRandom(), Rotation.NONE, placement.room.getStructure(), null, true, 16, maxHeight);
        }

        // anchor at the room's own corner, not this chunk's, then only let it write into this chunk
        BlockPos anchor = placement.originChunk.getBlockAt(0, y, 0);
        BoundingBox clip = roomChunks == 1 ? null : chunkBox(world, cpos);

        generatePiece(world, anchor, world.getRandom(), placement.room.data.rotation, placement.room.getStructure(),
                clip, placement.isOriginChunk(), roomChunks * 16, maxHeight);
        return true;
    }

    private static BoundingBox chunkBox(LevelAccessor world, ChunkPos cpos) {
        return new BoundingBox(cpos.getMinBlockX(), world.getMinBuildHeight(), cpos.getMinBlockZ(),
                cpos.getMaxBlockX(), world.getMaxBuildHeight(), cpos.getMaxBlockZ());
    }

}
