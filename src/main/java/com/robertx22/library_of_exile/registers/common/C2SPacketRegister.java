package com.robertx22.library_of_exile.registers.common;

import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.library_of_exile.packets.RequestTilePacket;
import com.robertx22.library_of_exile.registers.PacketChannel;

public class C2SPacketRegister {

    public static void register() {

        int id = 500;
        Packets.registerClientToServerPacket(PacketChannel.INSTANCE, new RequestTilePacket(), id++);

    }

}


