package com.robertx22.library_of_exile.database.affix.types;

import com.robertx22.library_of_exile.database.affix.apply_strat.ApplyStrategy;
import com.robertx22.library_of_exile.database.affix.apply_strat.AttributeApplyStrategy;
import com.robertx22.library_of_exile.database.affix.base.AffixTranslation;
import com.robertx22.library_of_exile.util.LazyClass;
import com.robertx22.library_of_exile.util.LazyStaticContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AttributeMobAffix extends ExileMobAffix {


    public static record Data(String attribute_id,
                              String uuid,
                              AttributeModifier.Operation operation,
                              AffixNumberRange number_range) {

        public static Data of(Attribute attribute, UUID uuid, AttributeModifier.Operation op, AffixNumberRange num) {
            return new Data(BuiltInRegistries.ATTRIBUTE.getKey(attribute).toString(), uuid.toString(), op, num);
        }
    }


    public Data data;

    public static class Lazy {
        public transient LazyClass<UUID> lazyUUID;
        public transient LazyClass<Attribute> lazyAttribute;

        public Lazy(AttributeMobAffix affix) {
            lazyUUID = new LazyClass<>(() -> UUID.fromString(affix.data.uuid));
            lazyAttribute = new LazyClass<>(() -> BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(affix.data.attribute_id)));
        }
    }

    static LazyStaticContainer<Lazy, AttributeMobAffix> LAZY = new LazyStaticContainer<>(true) {
        @Override
        public Lazy createData(AttributeMobAffix obj) {
            return new Lazy(obj);
        }
    };

    public AttributeMobAffix(Affects affects, String id, int weight, Data data, AffixTranslation t) {
        super("attribute_mob_affix", affects, id, weight, t);
        this.data = data;
    }


    public AttributeModifier getModifier(int perc) {
        var num = data.number_range.getNumber(perc);
        AttributeModifier mod = new AttributeModifier(
                LAZY.get(this).lazyUUID.get(),
                data.attribute_id,
                num,
                data.operation
        );
        return mod;
    }

    public void apply(int perc, LivingEntity en) {
        var mod = getModifier(perc);

        AttributeInstance atri = en.getAttribute(LAZY.get(this).lazyAttribute.get());
        if (atri != null) {
            if (atri.hasModifier(mod)) {
                atri.removeModifier(mod);
            }
            atri.addPermanentModifier(mod);
        }
    }

    public void remove(LivingEntity en) {
        var mod = getModifier(0);
        AttributeInstance atri = en.getAttribute(LAZY.get(this).lazyAttribute.get());
        if (atri != null) {
            if (atri.hasModifier(mod)) {
                atri.removeModifier(mod);
            }
        }
    }


    @Override
    public Class<AttributeMobAffix> getClassForSerialization() {
        return AttributeMobAffix.class;
    }


    @Override
    public ApplyStrategy getApplyStrategy() {
        return AttributeApplyStrategy.INSTANCE;
    }

    @Override
    public MutableComponent getParamName(int perc) {
        return getTooltip(LAZY.get(this).lazyAttribute.get(), getModifier(perc));

    }


    // copied from itemstack, hopefully better code exists in future versions
    public static MutableComponent getTooltip(Attribute a, AttributeModifier mod) {

        HashMap<Attribute, AttributeModifier> multimap = new HashMap();
        multimap.put(a, mod);

        for (Map.Entry<Attribute, AttributeModifier> entry : multimap.entrySet()) {
            AttributeModifier attributemodifier = entry.getValue();
            double d0 = attributemodifier.getAmount();
            boolean flag = false;

            double d1;
            if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                    d1 = d0 * 10.0D;
                } else {
                    d1 = d0;
                }
            } else {
                d1 = d0 * 100.0D;
            }

            if (flag) {
                return CommonComponents.space().append(Component.translatable("attribute.modifier.equals." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN);
            } else if (d0 > 0.0D) {
                return Component.translatable("attribute.modifier.plus." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.BLUE);
            } else if (d0 < 0.0D) {
                d1 *= -1.0D;
                return Component.translatable("attribute.modifier.take." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.RED);
            }
        }

        return Component.empty();

    }
}
