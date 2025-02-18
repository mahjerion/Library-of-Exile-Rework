package com.robertx22.library_of_exile.mixins;


import com.robertx22.orbs_of_crafting.main.OrbTooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin { // copied from TooltipCallback fabric event

    /// todo is this needed
    @Inject(method = {"getTooltipLines"}, at = {@At("RETURN")})
    private void getTooltip(Player entity, TooltipFlag tooltipContext, CallbackInfoReturnable<List<Component>> list) {
        try {
            ItemStack stack = (ItemStack) (Object) this;
            OrbTooltips.callMethod(stack, entity, tooltipContext, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
