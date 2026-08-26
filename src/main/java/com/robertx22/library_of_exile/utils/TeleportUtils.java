package com.robertx22.library_of_exile.utils;

import com.robertx22.library_of_exile.components.DelayedTeleportData;
import com.robertx22.library_of_exile.components.PlayerDataCapability;
import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import com.robertx22.library_of_exile.dimension.structure.SimplePrebuiltMapData;
import com.robertx22.library_of_exile.dimension.structure.SimplePrebuiltMapStructure;
import com.robertx22.library_of_exile.dimension.teleport.SavedTeleportPos;
import com.robertx22.library_of_exile.vanilla_util.main.VanillaUTIL;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;

public class TeleportUtils {

    public static boolean teleport(ServerPlayer player, BlockPos pos) {
        return teleport(player, pos, player.level().dimensionType());
    }

    public static boolean teleport(ServerPlayer player, BlockPos pos, DimensionType dimension) {
        return teleport(player, pos, VanillaUTIL.REGISTRY.dimensionTypes(player.level()).getKey(dimension));
    }

    /**
     * Schedules a teleport. Returns false when it did NOT schedule one, which callers must respect -
     * see the pending check below.
     */
    public static boolean teleport(ServerPlayer player, BlockPos pos, ResourceLocation dimension) {
        try {
            // todo is the gameprofile/uuid name correct?
            String command = "/execute in " + dimension.toString() + " run tp " + player.getStringUUID() +
                    " " + pos.getX() + " " + pos.getY() + " " + pos.getZ();

            var cap = PlayerDataCapability.get(player);

            // a teleport is already in flight for this player, so leave it alone. overwriting it drops
            // the pending instance's onArrival work - in a dungeon that is placeReturnDevice, so an
            // impatient second click on the map device during the "loading destination" wait used to
            // land the player in a dungeon with no device to get home from.
            if (cap.delayedTeleportData != null && cap.delayedTeleportData.isPending()) {
                return false;
            }

            cap.delayedTeleportData = new DelayedTeleportData(command, 2, SavedTeleportPos.from(dimension, pos));

            //CommandUtils.execute(player, command);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static BlockPos getSpawnTeleportPos(MapStructure<?> structure, BlockPos startAt) {
        BlockPos p = getOriginalTeleportPos(structure, startAt);
        if (structure instanceof SimplePrebuiltMapStructure simplePrebuiltMapStructure){
            SimplePrebuiltMapData map = simplePrebuiltMapStructure.getMap(structure.getStartChunkPos(startAt));
            p = p.offset(map.teleport_offset_x, map.teleport_offset_y, map.teleport_offset_z);
        }
        return p;
    }

    public static @NotNull BlockPos getOriginalTeleportPos(MapStructure<?> structure, BlockPos pos) {
        ChunkPos start = structure.getStartChunkPos(pos);
        BlockPos p = start.getMiddleBlockPosition(structure.getSpawnHeight() + 5);
        // shift from the start chunk's center to the actual room's center. 0 for single-chunk rooms,
        // so this is byte-identical for every existing map; only big-room dungeons override it.
        int off = structure.getSpawnCenterBlockOffset(start);
        p = new BlockPos(p.getX() + off, structure.getSpawnHeight() + 5, p.getZ() + off);
        return p;
    }
}


