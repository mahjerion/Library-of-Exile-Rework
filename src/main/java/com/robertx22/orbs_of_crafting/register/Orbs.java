package com.robertx22.orbs_of_crafting.register;

import com.robertx22.library_of_exile.deferred.RegObj;
import com.robertx22.library_of_exile.main.CommonInit;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.IdKey;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;
import com.robertx22.orbs_of_crafting.main.OrbsRef;
import net.minecraft.world.item.Item;

public class Orbs extends ExileKeyHolder<ExileCurrency> {

    // uh, how do i make sure the order of class instantiations is correct across mods..?

    public static Orbs INSTANCE = (Orbs) new Orbs(Ref.REGISTER_INFO)
            .itemIds(new ItemIdProvider(x -> OrbsRef.id(x)))
            .createItems(new ItemCreator<ExileCurrency>(x -> new Item(new Item.Properties().stacksTo(64))), x -> RegObj.register(x.itemID(), x.item(), CommonInit.ITEMS));

    public Orbs(ModRequiredRegisterInfo modRegisterInfo) {
        super(modRegisterInfo);
    }

    public ExileKey<ExileCurrency, IdKey> LEGENDARY_TOOL_ENCHANT = ExileCurrency.Builder.of("legendary_enchant_tool", "Orb of Legendary Tool Enchant")
            .addRequirement(Requirements.INSTANCE.IS_TOOL_TAG)
            .addRequirement(Requirements.INSTANCE.HAS_NO_ENCHANTS)
            .addAlwaysUseModification(Modifications.INSTANCE.ENCHANT_30_LEVELS)
            .potentialCost(0)
            .weight(0)
            .build(this);


    @Override
    public void loadClass() {

    }
}
