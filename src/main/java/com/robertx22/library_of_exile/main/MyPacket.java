package com.robertx22.library_of_exile.main;

import com.robertx22.library_of_exile.packets.ExilePacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class MyPacket<T> {
    FriendlyByteBuf buf = null;

    public abstract ResourceLocation getIdentifier();

    public abstract void loadFromData(FriendlyByteBuf tag);

    public abstract void saveToData(FriendlyByteBuf tag);

    public abstract void onReceived(ExilePacketContext ctx);

    public abstract MyPacket<T> newInstance();

    public final MyPacket loadFromDataUSETHIS(FriendlyByteBuf buf) {

        MyPacket<T> data = newInstance();
        try {
            this.buf = buf;

            data.loadFromData(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;

    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {

        ctx.get()
                .enqueueWork(
                        () -> {
                            try {
                                try {
                                    onReceived(new ExilePacketContext(ctx.get()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } finally {
                                if (buf != null) {
                                    buf.release();
                                }
                            }
                        });

        ctx.get().setPacketHandled(true);
    }

}
