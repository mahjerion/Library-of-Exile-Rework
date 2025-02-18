package com.robertx22.orbs_of_crafting.main;

import com.robertx22.orbs_of_crafting.lang.OrbWords;
import com.robertx22.orbs_of_crafting.register.ExileCurrency;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class OrbTooltips {

    public static void callMethod(ItemStack stack, Player entity, TooltipFlag tooltipContext, CallbackInfoReturnable<List<Component>> list) {
        if (Screen.hasControlDown()) {
            return;
        }
        // apparently i cant clear the entire thing with the forge event so.. ItemTooltipEvent

        var opt = ExileCurrency.get(stack);

        if (opt.isPresent()) {
            var cur = opt.get();


            if (cur != null) {
                list.getReturnValue().clear();
                list.getReturnValue().addAll(cur.getTooltip());

                if (entity != null && entity.isCreative()) {
                    list.getReturnValue().add(OrbWords.DRAG_NO_WORK_CREATIVE.get().withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                } else {
                    list.getReturnValue().add(OrbWords.DRAG_AND_DROP.get().withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD));

                }
            }
        }
    }
}
