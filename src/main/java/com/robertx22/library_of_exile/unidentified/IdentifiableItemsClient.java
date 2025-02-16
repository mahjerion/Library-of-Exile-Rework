package com.robertx22.library_of_exile.unidentified;

import com.robertx22.library_of_exile.main.ApiForgeEvents;
import com.robertx22.library_of_exile.main.LibWords;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;

public class IdentifiableItemsClient {
    public static void init() {


        ApiForgeEvents.registerForgeEvent(ItemTooltipEvent.class, event -> {
            try {
                ItemStack stack = event.getItemStack();
                var id = ForgeRegistries.ITEMS.getKey(stack.getItem());

                if (IdentifiableItems.map.containsKey(id)) {
                    if (IdentifiableItems.map.get(id).isUnidentified(stack)) {
                        event.getToolTip().addAll(
                                Arrays.asList(
                                        Component.literal("AAAAAAAAA").withStyle(ChatFormatting.BLUE, ChatFormatting.OBFUSCATED),
                                        LibWords.UNIDENTIFIED_ITEM.get().withStyle(ChatFormatting.BLUE),
                                        Component.literal("AAAAAAAAA").withStyle(ChatFormatting.BLUE, ChatFormatting.OBFUSCATED)
                                )
                        );
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}
