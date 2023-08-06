package com.robertx22.library_of_exile.utils;

import com.robertx22.library_of_exile.events.base.StaticServerPlayerTickEvent;
import com.robertx22.library_of_exile.vanilla_util.main.VanillaUTIL;
import net.minecraft.core.BlockPos;
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
        teleport(player, pos, dimension, true);
    }


    public static void teleport(ServerPlayer player, BlockPos pos, ResourceLocation dimension, Boolean addSafety) {
        try {


            ServerLevel world = (ServerLevel) VanillaUTIL.REGISTRY.dimensions(player.level()).get(dimension);

            if (world == null) {
                System.out.println("No world with id: " + dimension);
                return;
            }

            player.teleportTo(world, pos.getX(), pos.getY(), pos.getZ(), 0, 0);

            if (addSafety) {
                StaticServerPlayerTickEvent.makeSureTeleport(player, pos, dimension);
            }


            /*
            ServerCommandSource source = new ServerCommandSource(player, player.getPos(), player.getRotationClient(),
                player.world instanceof ServerLevel ? (ServerLevel) player.world : null, 5, player.getName()
                .getString(), player.getDisplayName(), player.world.getServer(), player).withSilent();

            String command = "/execute in " + dimension.toString() + " run tp " + "@p" +
                " " + pos.getX() + " " + pos.getY() + " " + pos.getZ();

            player
                .getServer()
                .getCommandManager()
                .execute(source, command);


             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


