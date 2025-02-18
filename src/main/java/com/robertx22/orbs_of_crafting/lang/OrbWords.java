package com.robertx22.orbs_of_crafting.lang;

import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.ITranslated;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.localization.TranslationType;
import com.robertx22.library_of_exile.main.Ref;
import net.minecraft.network.chat.MutableComponent;

import java.util.Locale;

public enum OrbWords implements ITranslated {
    DRAG_NO_WORK_CREATIVE("Item can not be used in Creative Mode!"),
    DRAG_AND_DROP("[Stack/Drag on another item to use]"),
    MOD_NAME("Orbs of Crafting"),
    Currency("Crafting Orb"),
    THIS_IS_NOT_A("This is not a %1$s"),
    POTENTIAL_COST("Potential Cost: %1$s"),
    NOT_A_POTENTIAL_CONSUMER("Does not consume potential when used"),
    Requirements("Requirements: "),
    OUTCOME_TIP("%1$s - %2$s"),
    ALWAYS_DOES("Always does:"),
    RANDOM_OUTCOME("Random Outcome:"),
    ;
    public String name;

    OrbWords(String name) {
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
        return "";
    }
}
