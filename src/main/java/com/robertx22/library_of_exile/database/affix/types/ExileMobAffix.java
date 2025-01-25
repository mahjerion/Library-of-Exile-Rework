package com.robertx22.library_of_exile.database.affix.types;

import com.robertx22.library_of_exile.custom_ser.CustomSerializer;
import com.robertx22.library_of_exile.custom_ser.GsonCustomSer;
import com.robertx22.library_of_exile.database.affix.apply_strat.ApplyStrategy;
import com.robertx22.library_of_exile.database.affix.base.AffixTranslation;
import com.robertx22.library_of_exile.database.init.ExileCustomSers;
import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.ITranslated;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.localization.TranslationType;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.registry.register_info.ExileRegistrationInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.Locale;
import java.util.UUID;

public abstract class ExileMobAffix implements JsonExileRegistry<ExileMobAffix>, GsonCustomSer<ExileMobAffix>, ITranslated {
    public static AttributeMobAffix SERIALIZER = new AttributeMobAffix(Affects.MOB, "", 0, AttributeMobAffix.Data.of(Attributes.ARMOR, UUID.randomUUID(), AttributeModifier.Operation.ADDITION, new AffixNumberRange(1, 1)), AffixTranslation.ofAttribute(Ref.MODID));

    public ExileMobAffix(String serializer, Affects affects, String id, int weight, AffixTranslation translation) {
        this.affects = affects;
        this.serializer = serializer;
        this.id = id;
        this.weight = weight;
        this.modid = translation.modid;
        this.locName = translation.locname;
    }

    public Affects affects = Affects.MOB;
    public String id = "";
    public String serializer = "";
    public int weight = 1000;

    public String modid = "";
    public transient String locName = "";

    public enum Affects implements ITranslated {
        PLAYER("Player", ChatFormatting.AQUA) {
            @Override
            public boolean is(LivingEntity en) {
                return en instanceof Player;
            }
        },
        MOB("Mob", ChatFormatting.RED) {
            @Override
            public boolean is(LivingEntity en) {
                return en instanceof Player == false;
            }
        };

        public String name;
        public ChatFormatting color;

        Affects(String name) {
            this.name = name;
        }

        Affects(String name, ChatFormatting color) {
            this.name = name;
            this.color = color;
        }

        @Override
        public TranslationBuilder createTranslationBuilder() {
            return TranslationBuilder.of(Ref.MODID).name(ExileTranslation.of(Ref.MODID + ".word." + name().toLowerCase(Locale.ROOT), name));
        }

        public abstract boolean is(LivingEntity en);
    }


    public abstract ApplyStrategy getApplyStrategy();

    @Override
    public void addToSerializables(ExileRegistrationInfo info) {
        getSerMap().register(this);
        Database.getRegistry(this.getExileRegistryType()).addSerializable(this, info);
    }

    public abstract MutableComponent getParamName(int perc);

    @Override
    public CustomSerializer getSerMap() {
        return ExileCustomSers.MOB_AFFIX;
    }

    public MutableComponent getPrefixedName(int perc) {
        var prefix = Component.literal("[").append(
                        affects.getTranslation(TranslationType.NAME).getTranslatedName())
                .append("] - ");
        prefix.withStyle(affects.color);

        var text = prefix.append(getParamName(perc));
        return text;
    }

    @Override
    public String getSer() {
        return serializer;
    }

    @Override
    public ExileRegistryType getExileRegistryType() {
        return LibDatabase.MOB_AFFIX;
    }

    @Override
    public String GUID() {
        return id;
    }

    @Override
    public int Weight() {
        return weight;
    }

    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(modid).name(ExileTranslation.registry(modid, this, locName));
    }

}
