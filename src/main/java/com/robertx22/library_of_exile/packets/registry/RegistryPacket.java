package com.robertx22.library_of_exile.packets.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.robertx22.library_of_exile.main.LibraryOfExile;
import com.robertx22.library_of_exile.main.MyPacket;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.packets.ExilePacketContext;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.registry.ListStringData;
import com.robertx22.library_of_exile.registry.RegistryPackets;
import com.robertx22.library_of_exile.utils.LoadSave;
import com.robertx22.library_of_exile.utils.Watch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class RegistryPacket extends MyPacket<RegistryPacket> {
    public static ResourceLocation ID = new ResourceLocation(Ref.MODID, "reg");

    public static final JsonParser PARSER = new JsonParser();

    ExileRegistryType type;
    ListStringData data;

    public RegistryPacket() {

    }

    public <T extends JsonExileRegistry> RegistryPacket(ExileRegistryType type, ListStringData data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return ID;
    }

    @Override
    public void loadFromData(FriendlyByteBuf tag) {

        try {
            type = ExileRegistryType.get(tag.readUtf(30));

            if (LibraryOfExile.runDevTools()) {
                //System.out.print("\n Gson packet " + type.name() + " is " + tag.readableBytes() + " bytes big\n");
            }
            CompoundTag nbt = tag.readNbt();

            data = LoadSave.Load(ListStringData.class, new ListStringData(), nbt, "data");

        } catch (Exception e) {
            System.out.println("Failed reading Age of Exile packet to bufferer.");
            e.printStackTrace();
        }

    }

    @Override
    public void saveToData(FriendlyByteBuf tag) {

        try {
            Watch watch = new Watch().min(8000);
            tag.writeUtf(type.id, 30);
            CompoundTag nbt = new CompoundTag();


            LoadSave.Save(data, nbt, "data");


            if (nbt.sizeInBytes() > 65535) {
                // it will crash, need to split it todo
            }

            watch.print("Writing gson packet for " + this.type.id + " ");
            tag.writeNbt(nbt);

        } catch (Exception e) {
            System.out.println("Failed saving " + type.id + " Age of Exile packet to bufferer.");
            e.printStackTrace();
        }

    }

    @Override
    public void onReceived(ExilePacketContext ctx) {

        if (data.getList()
                .isEmpty()) {
            throw new RuntimeException("Registry list sent from server is empty!");
        }

        List<JsonObject> set = RegistryPackets.get(type);

        data.getList()
                .forEach(x -> {
                    try {
                        JsonObject json = (JsonObject) PARSER.parse(x);
                        set.add(json);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();

                    }
                });

    }

    @Override
    public MyPacket<RegistryPacket> newInstance() {
        return new RegistryPacket();
    }
}