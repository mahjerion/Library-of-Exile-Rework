package com.robertx22.library_of_exile.database.map_finish_rarity;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.ITranslated;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.util.iTiered;
import net.minecraft.ChatFormatting;

public class MapFinishRarity implements JsonExileRegistry<MapFinishRarity>, IAutoGson<MapFinishRarity>, iTiered<MapFinishRarity>, ITranslated {
    public static MapFinishRarity SERIALIZER = new MapFinishRarity();

    public String id = "";

    public transient String locname = "";
    public transient String modid = "";

    public int perc_to_unlock = 0;
    public String loot_table = "";
    public int reward_chests = 1;
    public float mns_loot_multi = 1;
    public String text_format = "";

    public int tier = 0;

    public MapFinishRarity(String id, String locname, String modid, int perc_to_unlock, String loot_table, int reward_chests, float mns_loot_multi, String text_format, int tier) {
        this.id = id;
        this.locname = locname;
        this.modid = modid;
        this.perc_to_unlock = perc_to_unlock;
        this.loot_table = loot_table;
        this.reward_chests = reward_chests;
        this.mns_loot_multi = mns_loot_multi;
        this.text_format = text_format;
        this.tier = tier;
    }

    private MapFinishRarity() {
    }


    public ChatFormatting textFormatting() {
        try {
            return ChatFormatting.valueOf(text_format);
        } catch (Exception e) {
            //  e.printStackTrace();
        }
        return ChatFormatting.GRAY;
    }

    @Override
    public ExileRegistryType getExileRegistryType() {
        return LibDatabase.MAP_FINISH_RARITY;
    }

    @Override
    public Class<MapFinishRarity> getClassForSerialization() {
        return MapFinishRarity.class;
    }

    @Override
    public String GUID() {
        return id;
    }

    @Override
    public int Weight() {
        return 1000;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(Ref.MODID).name(ExileTranslation.registry(this, locname));
    }
}
