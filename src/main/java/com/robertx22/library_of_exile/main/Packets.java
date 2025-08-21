package com.robertx22.library_of_exile.main;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Optional;

public class Packets {

    static HashMap<ResourceLocation, SimpleChannel> channels = new HashMap<>();

    public static <T> void sendToClient(Player player, MyPacket<T> packet) {
        if (!(player instanceof ServerPlayer)) {
            ExileLog.get().debug("skip sending packet " + packet.getIdentifier().getPath() + "　to client!");
            return;
        }
        try {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            packet.saveToData(buf);

            channels.get(packet.getIdentifier())
                    .sendTo(
                            packet,
                            ((ServerPlayer) player).connection.connection,
                            NetworkDirection.PLAY_TO_CLIENT
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void sendToServer(MyPacket<T> packet) {
        try {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            packet.saveToData(buf);

            channels.get(packet.getIdentifier())
                    .sendToServer(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void registerClientToServerPacket(SimpleChannel channel, MyPacket<T> packet, int id) {
        channels.put(packet.getIdentifier(), channel);

        channel.registerMessage(
                id,
                (Class<MyPacket<T>>) packet.getClass(), // todo
                MyPacket::saveToData,
                packet::loadFromDataUSETHIS,
                MyPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
    }

    public static <T> void registerServerToClient(SimpleChannel channel, MyPacket<T> packet, int id) {
        channels.put(packet.getIdentifier(), channel);

        channel.registerMessage(
                id,
                (Class<MyPacket<T>>) packet.getClass(), // todo
                MyPacket::saveToData,
                packet::loadFromDataUSETHIS,
                MyPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );

        // ClientSidePacketRegistry.INSTANCE.register(packet.getIdentifier(), packet);
    }

    public static void sendToTracking(MyPacket msg, BlockPos pos, Level world) {
        channels.get(msg.getIdentifier())
                .send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), msg);
    }

    public static void sendToTracking(MyPacket msg, Entity en) {
        if (en.level().isClientSide) {

        } else {

            if (msg == null) {
                return;
            }

            channels.get(msg.getIdentifier())
                    .send(PacketDistributor.TRACKING_ENTITY.with(() -> en), msg);

        }
    }

}
