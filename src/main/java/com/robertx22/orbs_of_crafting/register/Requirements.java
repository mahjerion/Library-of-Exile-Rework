package com.robertx22.orbs_of_crafting.register;

import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;
import com.robertx22.orbs_of_crafting.register.reqs.HasNoEnchantsReq;
import com.robertx22.orbs_of_crafting.register.reqs.IsArmorReq;
import com.robertx22.orbs_of_crafting.register.reqs.IsDamagedReq;
import com.robertx22.orbs_of_crafting.register.reqs.IsSingleStack;
import com.robertx22.orbs_of_crafting.register.reqs.base.ItemRequirement;
import com.robertx22.orbs_of_crafting.register.reqs.vanilla.VanillaRequirement;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

public class Requirements extends ExileKeyHolder<ItemRequirement> {

    public static Requirements INSTANCE = new Requirements(Ref.REGISTER_INFO);

    public Requirements(ModRequiredRegisterInfo modRegisterInfo) {
        super(modRegisterInfo);
    }

    //vanilla
    public ExileKey<ItemRequirement, KeyInfo> IS_SINGLE_ITEM = ExileKey.ofId(this, "is_single_item", x -> new IsSingleStack(x.GUID()));
    public ExileKey<ItemRequirement, KeyInfo> IS_DAMAGED = ExileKey.ofId(this, "is_damaged", x -> new IsDamagedReq(x.GUID()));

    public ExileKey<ItemRequirement, KeyInfo> IS_TOOL_TAG = ExileKey.ofId(this, "is_tool_tag", x -> new VanillaRequirement(x.GUID(), "Must be tagged as #Tool", MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.TOOLS)).build()));
    public ExileKey<ItemRequirement, KeyInfo> IS_PICKAXE_TAG = ExileKey.ofId(this, "is_pickaxe_tag", x -> new VanillaRequirement(x.GUID(), "Must be tagged as #Pickaxe", MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.PICKAXES)).build()));
    public ExileKey<ItemRequirement, KeyInfo> IS_SWORD_TAG = ExileKey.ofId(this, "is_sword_tag", x -> new VanillaRequirement(x.GUID(), "Must be tagged as #Sword", MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.SWORDS)).build()));

    public ExileKey<ItemRequirement, KeyInfo> HAS_NO_ENCHANTS = ExileKey.ofId(this, "has_no_enchants", x -> new HasNoEnchantsReq(x.GUID()));

    public ExileKey<ItemRequirement, KeyInfo> IS_ARMOR = ExileKey.ofId(this, "is_armor", x -> new IsArmorReq(x.GUID()));


    @Override
    public void loadClass() {

    }
}
