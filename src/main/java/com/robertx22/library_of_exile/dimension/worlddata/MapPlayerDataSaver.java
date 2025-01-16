package com.robertx22.library_of_exile.dimension.worlddata;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

public class MapPlayerDataSaver<T> {

    private HashMap<String, T> map = new HashMap<String, T>();

    public T getData(Player p) {
        String id = p.getStringUUID();
        return map.get(id);
    }

    public boolean hasData(Player p) {
        return map.containsKey(p.getStringUUID());
    }

    public void setData(Player p, T data) {
        map.put(p.getStringUUID(), data);
    }
}
