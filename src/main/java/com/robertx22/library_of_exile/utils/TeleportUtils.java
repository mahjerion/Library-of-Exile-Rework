package com.robertx22.library_of_exile.utils;

import com.robertx22.library_of_exile.components.DelayedTeleportData;
import com.robertx22.library_of_exile.components.PlayerDataCapability;
import com.robertx22.library_of_exile.dimension.teleport.SavedTeleportPos;
import com.robertx22.library_of_exile.vanilla_util.main.VanillaUTIL;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.dimension.DimensionType;

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

            PlayerDataCapability.get(player).delayedTeleportData = new DelayedTeleportData(command, 3, SavedTeleportPos.from(dimension, pos));
            
            //CommandUtils.execute(player, command);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


