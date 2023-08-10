package com.robertx22.library_of_exile.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.dimension.DimensionType;

public class TeleportUtils {

    public static void teleport(ServerPlayer player, BlockPos pos) {
        teleport(player, pos, player.level().dimensionType());
    }

    public static void teleport(ServerPlayer player, BlockPos pos, DimensionType dimension) {
        teleport(player, pos, dimension);
    }


    public static void teleport(ServerPlayer player, BlockPos pos, ResourceLocation dimension) {
        try {


            ServerLevel world = player.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dimension));

            if (world == null) {
                System.out.println("No world with id: " + dimension);
                return;
            }

            String command = "/execute in " + dimension.toString() + " run tp " + "@p" +
                    " " + pos.getX() + " " + pos.getY() + " " + pos.getZ();

            CommandUtils.execute(player, command);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


