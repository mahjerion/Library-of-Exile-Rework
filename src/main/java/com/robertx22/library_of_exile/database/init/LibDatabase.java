package com.robertx22.library_of_exile.database.init;

import com.robertx22.library_of_exile.database.affix.types.ExileMobAffix;
import com.robertx22.library_of_exile.database.extra_map_content.MapContent;
import com.robertx22.library_of_exile.database.league.League;
import com.robertx22.library_of_exile.database.map_data_block.MapDataBlock;
import com.robertx22.library_of_exile.database.map_finish_rarity.MapFinishRarity;
import com.robertx22.library_of_exile.database.mob_list.MobList;
import com.robertx22.library_of_exile.database.relic.affix.RelicAffix;
import com.robertx22.library_of_exile.database.relic.relic_rarity.RelicRarity;
import com.robertx22.library_of_exile.database.relic.relic_type.RelicType;
import com.robertx22.library_of_exile.database.relic.stat.RelicStat;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryContainer;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.SyncTime;
import com.robertx22.library_of_exile.registry.helpers.ExileDatabaseInit;
import com.robertx22.orbs_of_crafting.register.ExileCurrency;
import com.robertx22.orbs_of_crafting.register.mods.base.ItemModification;
import com.robertx22.orbs_of_crafting.register.orb_edit.OrbEdit;
import com.robertx22.orbs_of_crafting.register.reqs.base.ItemRequirement;
import net.minecraft.data.CachedOutput;

public class LibDatabase extends ExileDatabaseInit {
    public static LibDatabase INSTANCE = new LibDatabase(Ref.MODID);

    public static ExileRegistryType<MapDataBlock> MAP_DATA_BLOCK = ExileRegistryType.register(Ref.MODID, "map_data_block", 0, MapDataBlock.SERIALIZER, SyncTime.NEVER);
    public static ExileRegistryType<MobList> MOB_LIST = ExileRegistryType.register(Ref.MODID, "mob_list", 0, MobList.SERIALIZER, SyncTime.NEVER);
    public static ExileRegistryType<ExileMobAffix> MOB_AFFIX = ExileRegistryType.register(Ref.MODID, "mob_affix", 0, ExileMobAffix.SERIALIZER, SyncTime.ON_LOGIN);
    public static ExileRegistryType<MapFinishRarity> MAP_FINISH_RARITY = ExileRegistryType.register(Ref.MODID, "map_finish_rar", 0, MapFinishRarity.SERIALIZER, SyncTime.ON_LOGIN);
    public static ExileRegistryType<MapContent> MAP_CONTENT = ExileRegistryType.register(Ref.MODID, "map_content", 0, MapContent.SERIALIZER, SyncTime.ON_LOGIN);
    public static ExileRegistryType<League> LEAGUE = ExileRegistryType.register(Ref.MODID, "league", 0, null, SyncTime.NEVER);

    // Atlas
    public static ExileRegistryType<RelicStat> RELIC_STAT = ExileRegistryType.register(Ref.MODID, "relic_stat", 0, RelicStat.SERIALIZER, SyncTime.ON_LOGIN);
    public static ExileRegistryType<RelicType> RELIC_TYPE = ExileRegistryType.register(Ref.MODID, "relic_type", 1, RelicType.SERIALIZER, SyncTime.ON_LOGIN);
    public static ExileRegistryType<RelicAffix> RELIC_AFFIX = ExileRegistryType.register(Ref.MODID, "relic_affix", 2, RelicAffix.SERIALIZER, SyncTime.ON_LOGIN);
    public static ExileRegistryType<RelicRarity> RELIC_RARITY = ExileRegistryType.register(Ref.MODID, "relic_rarity", 3, RelicRarity.SERIALIZER, SyncTime.ON_LOGIN);

    public static ExileRegistryType<ItemModification> ITEM_MOD = ExileRegistryType.register(Ref.MODID, "item_modification", 48, ItemModification.SERIALIZER, SyncTime.ON_LOGIN);
    public static ExileRegistryType<ItemRequirement> ITEM_REQ = ExileRegistryType.register(Ref.MODID, "item_requirement", 49, ItemRequirement.SERIALIZER, SyncTime.ON_LOGIN);
    public static ExileRegistryType<ExileCurrency> CURRENCY = ExileRegistryType.register(Ref.MODID, "currency", 50, ExileCurrency.SERIALIZER, SyncTime.ON_LOGIN);
    public static ExileRegistryType<OrbEdit> ORB_EDIT = ExileRegistryType.register(Ref.MODID, "orb_edit", 51, OrbEdit.SERIALIZER, SyncTime.ON_LOGIN);

    public LibDatabase(String modid) {
        super(modid);
    }

    public static ExileRegistryContainer<OrbEdit> OrbEdits() {
        return Database.getRegistry(ORB_EDIT);
    }

    public static ExileRegistryContainer<ExileCurrency> Currency() {
        return Database.getRegistry(CURRENCY);
    }

    public static ExileRegistryContainer<ItemModification> ItemMods() {
        return Database.getRegistry(ITEM_MOD);
    }

    public static ExileRegistryContainer<ItemRequirement> ItemReq() {
        return Database.getRegistry(ITEM_REQ);
    }

    @Override
    public void initDatabases() {

        Database.addRegistry(new ExileRegistryContainer<MapDataBlock>(MAP_DATA_BLOCK, "empty"));
        Database.addRegistry(new ExileRegistryContainer<MobList>(MOB_LIST, "empty"));
        Database.addRegistry(new ExileRegistryContainer<ExileMobAffix>(MOB_AFFIX, "empty"));
        Database.addRegistry(new ExileRegistryContainer<MapFinishRarity>(MAP_FINISH_RARITY, "common"));
        Database.addRegistry(new ExileRegistryContainer<MapContent>(MAP_CONTENT, "empty"));
        Database.addRegistry(new ExileRegistryContainer<League>(LEAGUE, "empty"));
        Database.addRegistry(new ExileRegistryContainer<RelicStat>(RELIC_STAT, "empty"));
        Database.addRegistry(new ExileRegistryContainer<RelicType>(RELIC_TYPE, "empty"));
        Database.addRegistry(new ExileRegistryContainer<RelicAffix>(RELIC_AFFIX, "empty"));
        Database.addRegistry(new ExileRegistryContainer<RelicRarity>(RELIC_RARITY, "common"));

        Database.addRegistry(new ExileRegistryContainer<ItemModification>(LibDatabase.ITEM_MOD, ""));
        Database.addRegistry(new ExileRegistryContainer<ItemRequirement>(LibDatabase.ITEM_REQ, ""));
        Database.addRegistry(new ExileRegistryContainer<OrbEdit>(LibDatabase.ORB_EDIT, ""));
        Database.addRegistry(new ExileRegistryContainer<ExileCurrency>(LibDatabase.CURRENCY, "socket_adder"));


    }

    public static ExileRegistryContainer<RelicRarity> RelicRarities() {
        return Database.getRegistry(RELIC_RARITY);
    }

    public static ExileRegistryContainer<RelicAffix> RelicAffixes() {
        return Database.getRegistry(RELIC_AFFIX);
    }


    public static ExileRegistryContainer<RelicType> RelicTypes() {
        return Database.getRegistry(RELIC_TYPE);
    }

    public static ExileRegistryContainer<RelicStat> RelicStats() {
        return Database.getRegistry(RELIC_STAT);
    }

    public static ExileRegistryContainer<League> Leagues() {
        return Database.getRegistry(LEAGUE);
    }

    public static ExileRegistryContainer<MapContent> MapContent() {
        return Database.getRegistry(MAP_CONTENT);
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

   
    @Override
    public void runDataGen(CachedOutput cache) {

    }
}
