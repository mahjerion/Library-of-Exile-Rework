package com.robertx22.library_of_exile.database.map_finish_rarity;

public class MapFinishRarityBuilder {
    private String id;
    private String locname;
    private String modid;
    private int perc_to_unlock;
    private String loot_table;
    private int reward_chests;
    private float mns_loot_multi;
    private String text_format;
    private int tier;

    public MapFinishRarityBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public MapFinishRarityBuilder setLocname(String locname) {
        this.locname = locname;
        return this;
    }

    public MapFinishRarityBuilder setModid(String modid) {
        this.modid = modid;
        return this;
    }

    public MapFinishRarityBuilder setPerc_to_unlock(int perc_to_unlock) {
        this.perc_to_unlock = perc_to_unlock;
        return this;
    }

    public MapFinishRarityBuilder setLoot_table(String loot_table) {
        this.loot_table = loot_table;
        return this;
    }

    public MapFinishRarityBuilder setReward_chests(int reward_chests) {
        this.reward_chests = reward_chests;
        return this;
    }

    public MapFinishRarityBuilder setMns_loot_multi(float mns_loot_multi) {
        this.mns_loot_multi = mns_loot_multi;
        return this;
    }

    public MapFinishRarityBuilder setText_format(String text_format) {
        this.text_format = text_format;
        return this;
    }

    public MapFinishRarityBuilder setTier(int tier) {
        this.tier = tier;
        return this;
    }

    public MapFinishRarity createMapFinishRarity() {
        return new MapFinishRarity(id, locname, modid, perc_to_unlock, loot_table, reward_chests, mns_loot_multi, text_format, tier);
    }
}