package com.robertx22.library_of_exile.database.relic.stat;

import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.TranslationBuilder;

public class ContentWeightRS extends RelicStat {

    public transient String modid;
    public transient String name;

    public String map_content_id;


    public ContentWeightRS(String id, String modid, String map_content_id, String name) {
        super("content_weight", id);
        this.modid = modid;
        this.map_content_id = map_content_id;
        this.name = name;

        this.setInfinitelyScalingPercentStat();
    }

    @Override
    public Class<?> getClassForSerialization() {
        return ContentWeightRS.class;
    }


    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(modid).name(ExileTranslation.registry(this, "Your maps are %1$s more likely to contain " + name + " Content"));
    }
}
