package com.robertx22.library_of_exile.database.init;

import com.robertx22.library_of_exile.database.map_data_block.MapDataBlock;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryContainer;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.SyncTime;
import com.robertx22.library_of_exile.registry.helpers.ExileDatabaseInit;
import net.minecraft.data.CachedOutput;

public class LibDatabase extends ExileDatabaseInit {
    public static LibDatabase INSTANCE = new LibDatabase(Ref.MODID);

    public static ExileRegistryType MAP_DATA_BLOCK = ExileRegistryType.register(Ref.MODID, "map_data_block", 0, MapDataBlock.SERIALIZER, SyncTime.NEVER);

    public LibDatabase(String modid) {
        super(modid);
    }

    @Override
    public void initDatabases() {
        Database.addRegistry(new ExileRegistryContainer<>(MAP_DATA_BLOCK, "empty"));

    }

    public static ExileRegistryContainer<MapDataBlock> MapDataBlocks() {
        return Database.getRegistry(MAP_DATA_BLOCK);
    }

    @Override
    public void registerGatherEvents() {

    }

    @Override
    public void runDataGen(CachedOutput cache) {

    }
}
