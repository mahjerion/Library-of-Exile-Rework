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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;

public class TeleportUtils {

    public static void teleport(ServerPlayer player, BlockPos pos) {
        teleport(player, pos, player.level().dimensionType());
    }

    public static void teleport(ServerPlayer player, BlockPos pos, DimensionType dimension) {
        teleport(player, pos, VanillaUTIL.REGISTRY.dimensionTypes(player.level()).getKey(dimension));
    }

    public static void teleport(ServerPlayer player, BlockPos pos, ResourceLocation dimension) {
        try {
            // todo is the gameprofile/uuid name correct?
            String command = "/execute in " + dimension.toString() + " run tp " + player.getStringUUID() +
                    " " + pos.getX() + " " + pos.getY() + " " + pos.getZ();

            PlayerDataCapability.get(player).delayedTeleportData = new DelayedTeleportData(command, 2, SavedTeleportPos.from(dimension, pos));

            //CommandUtils.execute(player, command);

        } catch (Exception e) {
            e.printStackTrace();
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
        BlockPos p = structure.getStartChunkPos(pos).getMiddleBlockPosition(structure.getSpawnHeight() + 5);
        p = new BlockPos(p.getX(), structure.getSpawnHeight() + 5, p.getZ());
        return p;
    }
}


