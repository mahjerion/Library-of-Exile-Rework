package com.robertx22.library_of_exile.database.relic.relic_rarity;

import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;

public class LibRelicRarities extends ExileKeyHolder<RelicRarity> {

    public static LibRelicRarities INSTANCE = new LibRelicRarities(Ref.REGISTER_INFO);

    public LibRelicRarities(ModRequiredRegisterInfo info) {
        super(info);
    }

    public ExileKey<RelicRarity, KeyInfo> COMMON = ExileKey.ofId(this, "common", x -> {
        RelicRarity r = new RelicRarity();

        r.base_data = BaseRarityData.common(Ref.MODID);

        r.affixes = 1;
        r.min_affix_percent = 0;
        r.max_affix_percent = 25;

        return r;
    });

    public ExileKey<RelicRarity, KeyInfo> UNCOMMON = ExileKey.ofId(this, "uncommon", x -> {
        RelicRarity r = new RelicRarity();
        r.base_data = BaseRarityData.uncommon(Ref.MODID);

        r.affixes = 1;
        r.min_affix_percent = 5;
        r.max_affix_percent = 50;


        return r;
    });

    public ExileKey<RelicRarity, KeyInfo> RARE = ExileKey.ofId(this, "rare", x -> {
        RelicRarity r = new RelicRarity();
        r.base_data = BaseRarityData.rare(Ref.MODID);

        r.affixes = 1;
        r.min_affix_percent = 10;
        r.max_affix_percent = 65;


        return r;
    });

    public ExileKey<RelicRarity, KeyInfo> EPIC = ExileKey.ofId(this, "epic", x -> {
        RelicRarity r = new RelicRarity();
        r.base_data = BaseRarityData.epic(Ref.MODID);

        r.affixes = 2;
        r.min_affix_percent = 15;
        r.max_affix_percent = 80;

        return r;
    });

    public ExileKey<RelicRarity, KeyInfo> LEGENDARY = ExileKey.ofId(this, "legendary", x -> {
        RelicRarity r = new RelicRarity();
        r.base_data = BaseRarityData.legendary(Ref.MODID);

        r.affixes = 2;
        r.min_affix_percent = 20;
        r.max_affix_percent = 100;

        return r;
    });

    public ExileKey<RelicRarity, KeyInfo> MYTHIC = ExileKey.ofId(this, "mythic", x -> {
        RelicRarity r = new RelicRarity();
        r.base_data = BaseRarityData.mythic(Ref.MODID);

        r.affixes = 3;
        r.min_affix_percent = 25;
        r.max_affix_percent = 100;

        return r;
    });


    @Override
    public void loadClass() {

    }
}
