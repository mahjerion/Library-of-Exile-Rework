package com.robertx22.library_of_exile.database.relic.stat;

import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.TranslationBuilder;

public class ManualRelicStat extends RelicStat {

    public transient String modid;
    public transient String name;


    public ManualRelicStat(String id, String modid, String locname) {
        super("manual_stat", id);
        this.modid = modid;
        this.name = locname;

        this.set100PercentStat();
    }

    @Override
    public Class<?> getClassForSerialization() {
        return ManualRelicStat.class;
    }


    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(modid).name(ExileTranslation.registry(this, name));
    }
}
