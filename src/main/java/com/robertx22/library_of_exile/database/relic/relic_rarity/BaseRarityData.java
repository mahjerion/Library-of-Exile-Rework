package com.robertx22.library_of_exile.database.relic.relic_rarity;

import net.minecraft.ChatFormatting;

public class BaseRarityData {

    public static BaseRarityData common(String modid) {
        return new BaseRarityData("common", modid, "Common", ChatFormatting.GRAY, 0, 5000);
    }

    public static BaseRarityData uncommon(String modid) {
        return new BaseRarityData("uncommon", modid, "Uncommon", ChatFormatting.GREEN, 1, 2500);
    }

    public static BaseRarityData rare(String modid) {
        return new BaseRarityData("rare", modid, "Rare", ChatFormatting.AQUA, 2, 1000);
    }

    public static BaseRarityData epic(String modid) {
        return new BaseRarityData("epic", modid, "Epic", ChatFormatting.LIGHT_PURPLE, 3, 750);
    }

    public static BaseRarityData legendary(String modid) {
        return new BaseRarityData("legendary", modid, "Legendary", ChatFormatting.GOLD, 4, 250);
    }

    public static BaseRarityData mythic(String modid) {
        return new BaseRarityData("mythic", modid, "Mythic", ChatFormatting.DARK_PURPLE, 5, 100);
    }


    public String id = "";

    public transient String locname = "";
    public transient String modid = "";

    public String text_format = "";
    public int tier = 0;

    public int weight = 1000;

    public BaseRarityData(String id, String modid, String locname, ChatFormatting text_format, int tier, int w) {
        this.id = id;
        this.locname = locname;
        this.modid = modid;
        this.text_format = text_format.name();
        this.tier = tier;
        this.weight = w;
    }

    public ChatFormatting color() {
        try {
            return ChatFormatting.valueOf(text_format);
        } catch (Exception e) {
            //  e.printStackTrace();
        }
        return ChatFormatting.GRAY;
    }
}
