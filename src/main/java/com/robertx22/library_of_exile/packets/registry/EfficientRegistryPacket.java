package com.robertx22.library_of_exile.packets.registry;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.robertx22.library_of_exile.main.MyPacket;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.packets.ExilePacketContext;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryContainer;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.registry.serialization.ISerializable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class EfficientRegistryPacket<T extends ISerializable & JsonExileRegistry> extends MyPacket<EfficientRegistryPacket> {
    public static final JsonParser PARSER = new JsonParser();

    public static ResourceLocation ID = new ResourceLocation(Ref.MODID, "eff_reg");
    private List<T> items;

    ExileRegistryType type;

    public EfficientRegistryPacket() {

    }

    public EfficientRegistryPacket(ExileRegistryType type, List<T> list) {
        this.type = type;
        this.items = list;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return ID;
    }

    @Override
    public void loadFromData(FriendlyByteBuf buf) {

        this.type = ExileRegistryType.get(buf.readUtf(40));

        ISerializable<T> serializer = type.getSerializer();

        this.items = Lists.newArrayList();

        int i = buf.readVarInt();

        for (int j = 0; j < i; ++j) {
            JsonObject json = (JsonObject) PARSER.parse(buf.readUtf(Integer.MAX_VALUE));
            this.items.add(serializer.fromJson(json));
        }
    }

    @Override
    public void saveToData(FriendlyByteBuf buf) {

        buf.writeUtf(type.id, 40);
        buf.writeVarInt(this.items.size());
        items.forEach(x -> {
            if (x.isFromDatapack()) {
                buf.writeUtf(x.toJsonString(), Integer.MAX_VALUE);
            }
        });
    }

    @Override
    public void onReceived(ExilePacketContext ctx) {

        ExileRegistryContainer reg = Database.getRegistry(type);


        items.forEach(x -> {
            x.unregisterFromExileRegistry();
            x.registerToExileRegistry();
        });

        System.out.println("Efficient " + type.id + " reg load on client success with: " + reg.getSize() + " entries.");

    }

    @Override
    public MyPacket<EfficientRegistryPacket> newInstance() {
        return new EfficientRegistryPacket();
    }
}