package com.robertx22.library_of_exile.utils;

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

            String command = "/execute in " + dimension.toString() + " run tp " + "@p" +
                    " " + pos.getX() + " " + pos.getY() + " " + pos.getZ();

            CommandUtils.execute(player, command);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


