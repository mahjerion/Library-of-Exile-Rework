package com.robertx22.library_of_exile.dimension.structure.dungeon;

import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.IWeighted;
import net.minecraft.resources.ResourceLocation;

public class DungeonRoom implements IWeighted {

    public ResourceLocation loc;
    public RoomType type;
    int weight = 1000;
    public boolean isBarrier = false;

    public DungeonRoom(String dun, String id, RoomType type) {
        this.loc = new ResourceLocation(Ref.MODID, "dun/" + dun + "/" + type.id + "/" + id);
        this.type = type;
    }

    public DungeonRoom weight(int weight) {
        this.weight = weight;
        return this;
    }

    @Override
    public int Weight() {
        return weight;
    }
}