package com.robertx22.library_of_exile.database.relic.stat;

import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.localization.TranslationType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

// Percent chance that a map is guaranteed to contain this map content. Unlike ContentWeightRS, which
// only nudges the weighted random pick, a successful roll here claims one of the map's bonus content
// slots outright before the random picks happen - see MapBonusContentsData.setupOnMapStart. The
// content stays in the random pool afterwards, so a ContentWeightRS for the same content can still
// win it further slots and isn't made redundant by the guarantee. A guarantee for content that can't
// spawn (gated by its min level config, or its mod isn't installed) simply does nothing and the slot
// goes back to being random.
public class GuaranteeContentRS extends RelicStat {

    public transient String modid;
    public transient String name;

    public String map_content_id;

    public GuaranteeContentRS(String id, String modid, String map_content_id, String name) {
        super("guarantee_content", id);
        this.modid = modid;
        this.map_content_id = map_content_id;
        this.name = name;

        this.set100PercentStat();
    }

    @Override
    public Class<?> getClassForSerialization() {
        return GuaranteeContentRS.class;
    }

    // at a full 100 the "% chance" phrasing is just noise, so show the flat guarantee instead
    @Override
    public MutableComponent getTooltip(float value) {
        if (value >= max) {
            return getTranslation(TranslationType.DESCRIPTION).getTranslatedName().withStyle(ChatFormatting.GREEN);
        }
        return super.getTooltip(value);
    }

    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(modid)
                .name(ExileTranslation.registry(this, "%1$s Chance to Add " + name + " To A Map"))
                .desc(ExileTranslation.of(guaranteedKey(), "Adds " + name + " To A Map"));
    }

    // separate lang key from the NAME one - TranslationBuilder maps by key, so reusing the registry
    // key for both would have the description silently overwrite the name in the lang file
    private String guaranteedKey() {
        var type = getExileRegistryType();
        return type.modid + "." + type.idWithoutModid + "." + GUID() + ".guaranteed";
    }
}
