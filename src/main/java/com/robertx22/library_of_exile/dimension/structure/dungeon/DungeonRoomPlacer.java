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
                .setIgnoreEntities(false)
                // see the comment on placeInWorld below - this is the switch that actually turns off
                // vanilla's neighbour shape fixup, and it is load bearing on both paths
                .setKnownShape(true);

        if (fireDataBlockEvents) {
            List<StructureTemplate.StructureBlockInfo> commandBlocks = template.filterBlocks(BlockPos.ZERO, new StructurePlaceSettings(), Blocks.COMMAND_BLOCK, true);
            List<StructureTemplate.StructureBlockInfo> structureBlocks = template.filterBlocks(BlockPos.ZERO, new StructurePlaceSettings(), Blocks.STRUCTURE_BLOCK, true);

            final BlockPos anchor = position;

            commandBlocks
            .forEach((block) -> {
                BlockPos worldPos = dataBlockWorldPos(anchor, rota, template, block.pos());

                var event = new ExileEvents.DungeonDataBlockPlaced(world, worldPos, block, id);
                ExileEvents.DUNGEON_DATA_BLOCK_PLACED.callEvents(event);
            });

            structureBlocks
            .forEach((block) -> {
                BlockPos worldPos = dataBlockWorldPos(anchor, rota, template, block.pos());

                var event = new ExileEvents.DungeonDataBlockPlaced(world, worldPos, block, id);
                ExileEvents.DUNGEON_DATA_BLOCK_PLACED.callEvents(event);
            });
        }

        settings.setBoundingBox(clip);

        // rotating a structure also moves it, so the anchor has to be pushed to compensate
        position = rotatedAnchor(position, rota, template);

        // The Block.UPDATE_KNOWN_SHAPE *flag* below does NOT skip vanilla's post placement shape pass -
        // it only changes what setBlock does internally. That pass is gated by the *setting*,
        // settings.getKnownShape(), which is why it is set above. (StructureTemplate.placeInWorld,
        // 1.20.1: `if (!settings.getKnownShape())` guards both updateShapeAtEdge and the per block
        // Block.updateFromNeighbourShapes loop.) The flag is kept because it is still correct for the
        // setBlock calls themselves.
        //
        // Skipping that pass matters for three separate reasons:
        //
        // 1. It runs updateShape on every placed block, including blocks from other mods. Some of them
        //    assume a real Level and NPE on a WorldGenRegion - tinymultiblocklib, bundled in
        //    moresnifferflowers, did exactly that and was 100% of the "Failed to generate 'dungeon'"
        //    errors on the live server. It throws AFTER every block is written but BEFORE the room's
        //    entities are added, so the room lands as a shell and isUncarved sees a carved chunk, which
        //    means the repair pass correctly refuses to touch it. Nothing downstream can recover from
        //    it; only not running the pass can.
        // 2. The pass reads AND writes one block outside every face of the room, and a room fills its
        //    chunk, so "one block outside" is the neighbouring chunk. On the repair path that runs on
        //    the server thread with no guarantee the neighbour is loaded, so it forces a blocking chunk
        //    load. MapGenerationUTIL fixed this for the arena/obelisk/harvest path and records it as
        //    ~54% of all time spent in ticks over 100ms; the dungeon path never got the same fix.
        // 3. It is one fewer neighbour chunk read+write per generated chunk.
        //
        // Vanilla's own jigsaw placement sets knownShape during worldgen for the same reasons. The cost
        // is that a fence, pane, wall or stair sitting exactly on a room's chunk seam no longer connects
        // to the room next door - acceptable for hand authored rooms that already store their own
        // connected states and meet at walls and doorways.
        var done = template.placeInWorld((ServerLevelAccessor) world, position, position, settings, random, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);

        return done;
    }

    /**
     * Where a room anchored at {@code anchor} actually has to be placed, because rotating a structure
     * also moves it. Rooms are anchored at their min corner, so a rotation pushes that corner along
     * the axes the rotation swept it out of.
     * <p>
     * Sizes are taken from the axis the rotation actually pulls the corner along, which is not the
     * same axis for the two quarter turns: placeInWorld rotates around the ZERO pivot, so a local
     * (x, z) lands at (-z, x) clockwise and (z, -x) counterclockwise. The corner therefore drops to
     * -(Z-1) on X clockwise, and to -(X-1) on Z counterclockwise. Every room shipped today is square,
     * which is why using the other axis has gone unnoticed, but warnAboutOddSize deliberately lets a
     * smaller room through, and that one would be placed a block off.
     */
    public static BlockPos rotatedAnchor(BlockPos anchor, Rotation rota, StructureTemplate template) {
        if (rota == Rotation.COUNTERCLOCKWISE_90) {
            // west: rotate CCW and push +Z by the footprint's X extent
            return anchor.offset(0, 0, template.getSize().getX() - 1);
        }
        if (rota == Rotation.CLOCKWISE_90) {
            // east: rotate CW and push +X by the footprint's Z extent
            return anchor.offset(template.getSize().getZ() - 1, 0, 0);
        }
        if (rota == Rotation.CLOCKWISE_180) {
            // south: rotate 180, each axis pushed by its own extent
            return anchor.offset(template.getSize().getX() - 1, 0, template.getSize().getZ() - 1);
        }
        // north: no rotation
        return anchor;
    }

    /**
     * World position a block of {@code template} ends up at once the room is placed at {@code anchor}
     * with {@code rota}. This is the same math placeInWorld does internally (rotate around the default
     * ZERO pivot, then offset by the placed position), so it can be used to locate a data block either
     * while placing the room or without placing it at all.
     */
    public static BlockPos dataBlockWorldPos(BlockPos anchor, Rotation rota, StructureTemplate template, BlockPos localPos) {
        var settings = new StructurePlaceSettings().setRotation(rota);
        return rotatedAnchor(anchor, rota, template).offset(StructureTemplate.calculateRelativePosition(settings, localPos));
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
            // shared cache/build path with the map_bug report, so both resolve the identical layout.
            // the caller already resolved this builder for this chunk, so hand it over rather than
            // making the cache resolve a second one.
            builder.builtDungeon = dungeonStruc.getBuiltDungeon(start, builder);
            if (builder.builtDungeon == null) {
                // the instance's map data can't be read, so there is no layout that is safe to write
                // here. Leave the chunk as the bedrock it already is - DungeonStructure.getBuiltDungeon
                // explains why, and repairChunksAround carves it once the data is readable.
                return false;
            }
        } else {
            builder.build();
        }

        // room size has to come from the dungeon the grid was actually built from. the passed builder can
        // have resolved a different dungeon than the cached layout (different room size = every room
        // anchored to the wrong chunk), so the layout's own builder wins whenever there is one.
        int roomChunks = builder.builtDungeon.b != null ? builder.builtDungeon.b.getRoomChunks() : builder.getRoomChunks();

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
