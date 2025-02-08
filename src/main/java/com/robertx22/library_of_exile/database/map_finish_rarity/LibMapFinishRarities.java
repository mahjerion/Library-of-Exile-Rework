package com.robertx22.library_of_exile.database.map_finish_rarity;

import com.robertx22.library_of_exile.main.LibLootTables;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;
import net.minecraft.ChatFormatting;

public class LibMapFinishRarities extends ExileKeyHolder<MapFinishRarity> {

    public static LibMapFinishRarities INSTANCE = new LibMapFinishRarities(Ref.REGISTER_INFO);

    public LibMapFinishRarities(ModRequiredRegisterInfo modRegisterInfo) {
        super(modRegisterInfo);
    }

    public ExileKey<MapFinishRarity, KeyInfo> COMMON = ExileKey.ofId(this, "common", x -> {
        MapFinishRarity b = new MapFinishRarityBuilder()
                .setId(x.GUID())
                .setPerc_to_unlock(0)
                .setLocname("Common")
                .setModid(Ref.MODID)
                .setTier(0)
                .setLoot_table(LibLootTables.TIER_1_DUNGEON_CHEST.toString())
                .setMns_loot_multi(1)
                .setReward_chests(1)
                .setText_format(ChatFormatting.GRAY.name())
                .createMapFinishRarity();
        return b;
    });
    public ExileKey<MapFinishRarity, KeyInfo> UNCOMMON = ExileKey.ofId(this, "uncommon", x -> {
        MapFinishRarity b = new MapFinishRarityBuilder()
                .setId(x.GUID())
                .setPerc_to_unlock(10)
                .setLocname("Uncommon")
                .setModid(Ref.MODID)
                .setTier(1)
                .setLoot_table(LibLootTables.TIER_1_DUNGEON_CHEST.toString())
                .setMns_loot_multi(1)
                .setReward_chests(1)
                .setText_format(ChatFormatting.GREEN.name())
                .createMapFinishRarity();
        return b;
    });

    public ExileKey<MapFinishRarity, KeyInfo> RARE = ExileKey.ofId(this, "rare", x -> {
        MapFinishRarity b = new MapFinishRarityBuilder()
                .setId(x.GUID())
                .setPerc_to_unlock(25)
                .setLocname("Rare")
                .setModid(Ref.MODID)
                .setTier(2)
                .setLoot_table(LibLootTables.TIER_2_DUNGEON_CHEST.toString())
                .setMns_loot_multi(1.5F)
                .setReward_chests(2)
                .setText_format(ChatFormatting.AQUA.name())
                .createMapFinishRarity();
        return b;
    });

    public ExileKey<MapFinishRarity, KeyInfo> EPIC = ExileKey.ofId(this, "epic", x -> {
        MapFinishRarity b = new MapFinishRarityBuilder()
                .setId(x.GUID())
                .setPerc_to_unlock(50)
                .setLocname("Epic")
                .setModid(Ref.MODID)
                .setTier(3)
                .setLoot_table(LibLootTables.TIER_3_DUNGEON_CHEST.toString())
                .setMns_loot_multi(2)
                .setReward_chests(3)
                .setText_format(ChatFormatting.LIGHT_PURPLE.name())
                .createMapFinishRarity();
        return b;
    });

    public ExileKey<MapFinishRarity, KeyInfo> LEGENDARY = ExileKey.ofId(this, "legendary", x -> {
        MapFinishRarity b = new MapFinishRarityBuilder()
                .setId(x.GUID())
                .setPerc_to_unlock(75)
                .setLocname("Legendary")
                .setModid(Ref.MODID)
                .setTier(4)
                .setLoot_table(LibLootTables.TIER_4_DUNGEON_CHEST.toString())
                .setMns_loot_multi(2.5F)
                .setReward_chests(5)
                .setText_format(ChatFormatting.GOLD.name())
                .createMapFinishRarity();
        return b;
    });
    public ExileKey<MapFinishRarity, KeyInfo> MYTHIC = ExileKey.ofId(this, "mythic", x -> {
        MapFinishRarity b = new MapFinishRarityBuilder()
                .setId(x.GUID())
                .setPerc_to_unlock(90)
                .setLocname("Mythic")
                .setModid(Ref.MODID)
                .setTier(5)
                .setLoot_table(LibLootTables.TIER_5_DUNGEON_CHEST.toString())
                .setMns_loot_multi(3)
                .setReward_chests(6)
                .setText_format(ChatFormatting.DARK_PURPLE.name())
                .createMapFinishRarity();
        return b;
    });


    @Override
    public void loadClass() {

    }
}
