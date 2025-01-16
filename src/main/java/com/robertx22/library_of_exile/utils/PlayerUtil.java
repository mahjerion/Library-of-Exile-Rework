package com.robertx22.library_of_exile.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerUtil {
    public static void giveItem(ItemStack stack, Player player) {
        if (player.addItem(stack) == false) {
            player.spawnAtLocation(stack, 1F);
        }
        player.getInventory().setChanged();
    }
}
