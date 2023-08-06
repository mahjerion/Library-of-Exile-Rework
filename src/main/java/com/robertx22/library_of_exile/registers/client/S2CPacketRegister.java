package com.robertx22.library_of_exile.registers.client;

import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.library_of_exile.packets.SyncPlayerCapToClient;
import com.robertx22.library_of_exile.packets.TileUpdatePacket;
import com.robertx22.library_of_exile.packets.particles.ParticlePacket;
import com.robertx22.library_of_exile.packets.registry.EfficientRegistryPacket;
import com.robertx22.library_of_exile.packets.registry.RegistryPacket;
import com.robertx22.library_of_exile.packets.registry.TellClientToRegisterFromPackets;
import com.robertx22.library_of_exile.registers.PacketChannel;

public class S2CPacketRegister {

    public static void register() {

        int id = 100;

        Packets.registerServerToClient(PacketChannel.INSTANCE, new SyncPlayerCapToClient(), id++);
        Packets.registerServerToClient(PacketChannel.INSTANCE, new EfficientRegistryPacket<>(), id++);
        Packets.registerServerToClient(PacketChannel.INSTANCE, new RegistryPacket(), id++);
        Packets.registerServerToClient(PacketChannel.INSTANCE, new TellClientToRegisterFromPackets(), id++);
        Packets.registerServerToClient(PacketChannel.INSTANCE, new ParticlePacket(), id++);
        Packets.registerServerToClient(PacketChannel.INSTANCE, new TileUpdatePacket(), id++);

        //  ClientSidePacketRegistry.INSTANCE.register(EntityPacket.ID, (ctx, buf) -> {
        //     EntityPacketOnClient.onPacket(ctx, buf);
        //});

    }
}
