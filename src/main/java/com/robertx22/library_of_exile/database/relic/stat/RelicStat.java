package com.robertx22.library_of_exile.database.relic.stat;

import com.robertx22.library_of_exile.custom_ser.CustomSerializer;
import com.robertx22.library_of_exile.custom_ser.GsonCustomSer;
import com.robertx22.library_of_exile.database.affix.base.AffixTranslation;
import com.robertx22.library_of_exile.database.init.ExileCustomSers;
import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.localization.ITranslated;
import com.robertx22.library_of_exile.localization.TranslationType;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.registry.register_info.ExileRegistrationInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

public abstract class RelicStat implements JsonExileRegistry<RelicStat>, GsonCustomSer<RelicStat>, ITranslated {

    public static EmptyRelicStat SERIALIZER = new EmptyRelicStat("empty");

    public String id;
    public String serializer;

    public boolean is_percent = true;

    public float min = 0;
    public float max = 1000;
    public float base = 0;

    public RelicStat(String ser, String id) {
        this.id = id;
        this.serializer = ser;
    }

    public void setInfinitelyScalingPercentStat() {
        is_percent = true;
        min = 0;
        max = 1000;
        base = 0;
    }

    public void set100PercentStat() {
        is_percent = true;
        min = 0;
        max = 100;
        base = 0;
    }

    public float cap(float val) {
        return Mth.clamp(val, min, max);
    }

    @Override
    public void addToSerializables(ExileRegistrationInfo info) {
        getSerMap().register(this);
        Database.getRegistry(this.getExileRegistryType()).addSerializable(this, info);
    }

    @Override
    public CustomSerializer<RelicStat> getSerMap() {
        return ExileCustomSers.ATLAS_STAT;
    }

    @Override
    public String getSer() {
        return serializer;
    }

    public MutableComponent getTooltip(float value) {
        String num = formatNumber(value);
        String perc = this.is_percent ? "%" : "";
        String plusminus = value > 0 ? "+" : "";

        ChatFormatting color = ChatFormatting.YELLOW;

        String finalNum = plusminus + num + perc;

        var numcomp = Component.literal(finalNum).withStyle(color);

        return getTranslation(TranslationType.NAME).getTranslatedName(numcomp).withStyle(ChatFormatting.GREEN);
    }

    static String formatNumber(float num) {
        if (num < 10) {
            return AffixTranslation.DECIMAL_FORMAT.format(num);
        } else {
            return ((int) num) + "";
        }
    }

    @Override
    public ExileRegistryType getExileRegistryType() {
        return LibDatabase.RELIC_STAT;
    }

    @Override
    public String GUID() {
        return id;
    }

    @Override
    public int Weight() {
        return 1000;
    }


}
