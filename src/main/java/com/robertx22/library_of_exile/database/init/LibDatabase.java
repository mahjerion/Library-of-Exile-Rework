package com.robertx22.library_of_exile.database.init;

import com.robertx22.library_of_exile.database.affix.types.ExileMobAffix;
import com.robertx22.library_of_exile.database.invis_block.InvisibleData;
import com.robertx22.library_of_exile.database.map_data_block.MapDataBlock;
import com.robertx22.library_of_exile.database.map_finish_rarity.MapFinishRarity;
import com.robertx22.library_of_exile.database.mob_list.MobList;
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
    public static ExileRegistryType MOB_LIST = ExileRegistryType.register(Ref.MODID, "mob_list", 0, MobList.SERIALIZER, SyncTime.NEVER);
    public static ExileRegistryType MOB_AFFIX = ExileRegistryType.register(Ref.MODID, "mob_affix", 0, ExileMobAffix.SERIALIZER, SyncTime.ON_LOGIN);
    public static ExileRegistryType INVISIBLE_DATA = ExileRegistryType.register(Ref.MODID, "invisible_data", 0, null, SyncTime.NEVER);
    public static ExileRegistryType MAP_FINISH_RARITY = ExileRegistryType.register(Ref.MODID, "map_finish_rar", 0, MapFinishRarity.SERIALIZER, SyncTime.ON_LOGIN);

    public LibDatabase(String modid) {
        super(modid);
    }

    @Override
    public void initDatabases() {

        Database.addRegistry(new ExileRegistryContainer<>(MAP_DATA_BLOCK, "empty"));
        Database.addRegistry(new ExileRegistryContainer<>(MOB_LIST, "empty"));
        Database.addRegistry(new ExileRegistryContainer<>(MOB_AFFIX, "empty"));
        Database.addRegistry(new ExileRegistryContainer<>(INVISIBLE_DATA, "empty"));
        Database.addRegistry(new ExileRegistryContainer<>(MAP_FINISH_RARITY, "common"));

    }

    public static ExileRegistryContainer<MapFinishRarity> MapFinishRarity() {
        return Database.getRegistry(MAP_FINISH_RARITY);
    }

    public static ExileRegistryContainer<MapDataBlock> MapDataBlocks() {
        return Database.getRegistry(MAP_DATA_BLOCK);
    }

    public static ExileRegistryContainer<MobList> MobLists() {
        return Database.getRegistry(MOB_LIST);
    }

    public static ExileRegistryContainer<ExileMobAffix> MobAffixes() {
        return Database.getRegistry(MOB_AFFIX);
    }

    public static ExileRegistryContainer<InvisibleData> InvisibleData() {
        return Database.getRegistry(INVISIBLE_DATA);
    }


    @Override
    public void runDataGen(CachedOutput cache) {

    }
}
