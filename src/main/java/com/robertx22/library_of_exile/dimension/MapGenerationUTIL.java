package com.robertx22.library_of_exile.dimension;

import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MapGenerationUTIL {
    public static Random createRandom(Level level, ChunkPos start) {
        long worldSeed = level.getServer().getWorldData().worldGenOptions().seed();
        int chunkX = start.x;
        int chunkZ = start.z;
        long newSeed = (worldSeed + (long) (chunkX * chunkX * 4987142) + (long) (chunkX * 5947611) + (long) (chunkZ * chunkZ) * 4392871L + (long) (chunkZ * 389711) ^ worldSeed);
        return new Random(newSeed);
    }

    public static Random createRandom(ChunkPos start) {
        return createRandom(ServerLifecycleHooks.getCurrentServer().overworld(), start);
    }

    // a map chunk starts as solid bedrock and is only ever carved once, so a template that fails to
    // place leaves a permanent bedrock hole in an arena or a dungeon. this used to be reported only
    // when the caller opted in, and the one caller that mattered opted out - which is why holes were
    // only ever found by players walking into them. it always reports now.
    //
    // rate limited per template rather than per call: a missing room is asked for once per chunk of
    // every instance that rolls that arena, which would be thousands of identical lines a session.
    private static final Set<ResourceLocation> WARNED_MISSING = ConcurrentHashMap.newKeySet();

    // a wipe/reload can bring a datapack's structures back, so a template that reported missing has
    // to be able to report again instead of staying quiet about a second, real failure.
    public static void forgetMissingStructureWarnings() {
        WARNED_MISSING.clear();
    }

    public static boolean spawnStructure(ServerLevelAccessor level, ChunkPos cpos, StructureTemplateManager man, int y, ResourceLocation room) {

        try {

            var opt = man.get(room);
            if (opt.isEmpty() || opt.get() == null) {
                warnMissing(room, cpos, y, "does not exist");
                return false;
            }

            var template = opt.get();

            if (template.getSize().getX() > 16 || template.getSize().getZ() > 16) {
                warnMissing(room, cpos, y, "is bigger than one chunk " + template.getSize());
                return false;
            }

            StructurePlaceSettings settings = new StructurePlaceSettings().setMirror(Mirror.NONE).setIgnoreEntities(false);
            settings.setBoundingBox(settings.getBoundingBox());
            settings.setRotation(Rotation.NONE);

            BlockPos position = cpos.getBlockAt(0, y, 0);

            template.placeInWorld(level, position, position, settings, level.getRandom(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);

        } catch (Exception e) {
            ExileLog.get().error("Failed to place structure (" + room + ") at " + cpos + " y=" + y
                    + ". That chunk stays solid bedrock, and generation never runs there again.", e);
            return false;
        }

        return true;
    }

    private static void warnMissing(ResourceLocation room, ChunkPos cpos, int y, String problem) {
        if (WARNED_MISSING.add(room)) {
            ExileLog.get().warn("Map structure (" + room + ") " + problem + ", first seen at " + cpos + " y=" + y
                    + ". Every chunk that wants it stays solid bedrock. Further reports for this structure are suppressed.");
        }
    }

}
