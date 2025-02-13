package com.robertx22.library_of_exile.database.map_data_block;


import com.robertx22.library_of_exile.custom_ser.CustomSerializer;
import com.robertx22.library_of_exile.custom_ser.GsonCustomSer;
import com.robertx22.library_of_exile.database.init.ExileCustomSers;
import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.database.map_data_block.all.EmptyMBlock;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.registry.register_info.ExileRegistrationInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public abstract class MapDataBlock implements JsonExileRegistry<MapDataBlock>, GsonCustomSer<MapDataBlock> {

    public static MapDataBlock SERIALIZER = new EmptyMBlock("empty");

    public String serializer = "";
    public String id = "";
    public int weight = 1000;
    public IdMatchType id_match_type = IdMatchType.EQUALS;
    public List<String> aliases = new ArrayList<>();
    public List<String> tags = new ArrayList<>();

    public MapDataBlock(String serializer, String id) {
        this.serializer = serializer;
        this.id = id;
    }

    public enum IdMatchType {
        EQUALS, CONTAINS
    }

    public final boolean process(String key, BlockPos pos, Level world, CompoundTag nbt) {

        if (id_match_type == IdMatchType.EQUALS && (this.id.equals(key) || aliases.contains(key))) {
            processImplementationINTERNAL(key, pos, world, nbt);
            ExileEvents.PROCESS_DATA_BLOCK.callEvents(new ExileEvents.OnProcessMapDataBlock(this, key, pos, world, nbt));
            // trySpawnLeagueMechanicIfCan(map, world, pos);
            return true;
        } else if (id_match_type == IdMatchType.CONTAINS && (this.id.contains(key) || aliases.stream().anyMatch(key::contains))) {
            processImplementationINTERNAL(key, pos, world, nbt);
            ExileEvents.PROCESS_DATA_BLOCK.callEvents(new ExileEvents.OnProcessMapDataBlock(this, key, pos, world, nbt));
            // trySpawnLeagueMechanicIfCan(map, world, pos);
            return true;
        }
        return false;
    }

    @Override
    public void addToSerializables(ExileRegistrationInfo info) {
        getSerMap().register(this);
        Database.getRegistry(this.getExileRegistryType()).addSerializable(this, info);
    }

    public abstract void processImplementationINTERNAL(String key, BlockPos pos, Level world, CompoundTag nbt);

    @Override
    public CustomSerializer getSerMap() {
        return ExileCustomSers.MAP_DATA_BLOCK;
    }

    @Override
    public String getSer() {
        return serializer;
    }

    @Override
    public ExileRegistryType getExileRegistryType() {
        return LibDatabase.MAP_DATA_BLOCK;
    }

    @Override
    public String GUID() {
        return id;
    }

    @Override
    public int Weight() {
        return weight;
    }
}


