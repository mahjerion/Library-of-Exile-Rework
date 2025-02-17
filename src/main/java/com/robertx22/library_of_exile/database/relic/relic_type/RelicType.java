package com.robertx22.library_of_exile.database.relic.relic_type;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.ITranslated;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.util.TranslateInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class RelicType implements ITranslated, IAutoGson<RelicType>, JsonExileRegistry<RelicType> {
    public static EmptyRelicType SERIALIZER = new EmptyRelicType("empty");

    public String id;

    private transient String name = "";
    private transient String modid = "";

    public String item_id = "";

    public int weight = 1000;

    public int max_equipped = 3;

    public RelicType(String id, TranslateInfo name) {
        this.id = id;
        this.name = name.name;
        this.modid = name.modid;
    }


    public Item getItem() {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(item_id));
    }

    @Override
    public ExileRegistryType getExileRegistryType() {
        return LibDatabase.RELIC_TYPE;
    }

    @Override
    public Class<RelicType> getClassForSerialization() {
        return RelicType.class;
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
        // hm, probably should have separated things a bit here
        return TranslationBuilder.of(modid).name(ExileTranslation.registry(this, name));
    }
}
