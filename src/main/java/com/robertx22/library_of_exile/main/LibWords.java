package com.robertx22.library_of_exile.main;

import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.ITranslated;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.localization.TranslationType;
import net.minecraft.network.chat.MutableComponent;

import java.util.Locale;

public enum LibWords implements ITranslated {
    UNIDENTIFIED_ITEM("Unidentified. Use to Identify it!");

    public String name;

    LibWords(String name) {
        this.name = name;
    }

    @Override
    public TranslationBuilder createTranslationBuilder() {
        return new TranslationBuilder(Ref.MODID).name(ExileTranslation.of(Ref.MODID + ".words." + this.name().toLowerCase(Locale.ROOT), name));
    }

    public MutableComponent get(Object... obj) {
        return getTranslation(TranslationType.NAME).getTranslatedName(obj);
    }

    @Override
    public String GUID() {
        return name();
    }
}
