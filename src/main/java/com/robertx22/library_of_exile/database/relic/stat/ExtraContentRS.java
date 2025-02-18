package com.robertx22.library_of_exile.database.relic.stat;

import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.TranslationBuilder;

public class ExtraContentRS extends RelicStat {

    public transient String modid;
    public transient String contentName;

    public Data data;

    public enum Type {
        ADDITION(" more"), MULTIPLY("x");

        String term;

        Type(String term) {
            this.term = term;
        }
    }

    public static record Data(Type type, int extra, String map_content_id) {
    }

    public ExtraContentRS(String id, String modid, String contentName, Data data) {
        super("extra_content", id);
        this.modid = modid;
        this.contentName = contentName;
        this.data = data;

        this.set100PercentStat();
    }

    @Override
    public Class<?> getClassForSerialization() {
        return ExtraContentRS.class;
    }


    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(modid).name(ExileTranslation.registry(this, "%1$s chance to contain " + data.extra + data.type.term + " " + contentName));
    }
}
