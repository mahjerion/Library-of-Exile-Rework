package com.robertx22.library_of_exile.database.relic.relic_rarity;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.ITranslated;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.util.iTiered;

public class RelicRarity implements JsonExileRegistry<RelicRarity>, IAutoGson<RelicRarity>, iTiered<RelicRarity>, ITranslated {
    public static RelicRarity SERIALIZER = new RelicRarity();

    public BaseRarityData base_data = BaseRarityData.common(Ref.MODID);

    public int affixes = 0;
    public int min_affix_percent = 0;
    public int max_affix_percent = 100;


    @Override
    public ExileRegistryType getExileRegistryType() {
        return LibDatabase.RELIC_RARITY;
    }

    @Override
    public Class<RelicRarity> getClassForSerialization() {
        return RelicRarity.class;
    }

    @Override
    public String GUID() {
        return base_data.id;
    }

    @Override
    public int Weight() {
        return base_data.weight;
    }

    @Override
    public int getTier() {
        return base_data.tier;
    }

    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(base_data.modid).name(ExileTranslation.registry(this, base_data.locname));
    }
}
