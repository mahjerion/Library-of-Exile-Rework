package com.robertx22.library_of_exile.database.relic.stat;

import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.main.Ref;

public class EmptyRelicStat extends RelicStat {

    public EmptyRelicStat(String id) {
        super(id, id);
    }

    @Override
    public Class<?> getClassForSerialization() {
        return EmptyRelicStat.class;
    }

    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(Ref.MODID).name(ExileTranslation.registry(this, "empty stat"));
    }
}
