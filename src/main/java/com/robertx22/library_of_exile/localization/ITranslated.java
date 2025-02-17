package com.robertx22.library_of_exile.localization;

import com.robertx22.library_of_exile.registry.IGUID;

public interface ITranslated extends IGUID {

    public TranslationBuilder createTranslationBuilder();

    public default ExileTranslation getTranslation(TranslationType type) {
        return createTranslationBuilder().all.get(type);
    }
}
