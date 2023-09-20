package com.robertx22.library_of_exile.mixin_methods;

import com.robertx22.library_of_exile.events.base.ExileEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class OnBlockDropFarming {
    public static void run(LootContext ctx, CallbackInfoReturnable<List<ItemStack>> ci) {

        try {
            if (!ctx.hasParam(LootContextParams.BLOCK_STATE)) {
                return;
            }
            if (!ctx.hasParam(LootContextParams.ORIGIN)) {
                return;
            }
            if (!ctx.hasParam(LootContextParams.THIS_ENTITY)) {
                return;
            }

            ItemStack stack = ctx.getParamOrNull(LootContextParams.TOOL);
            if (stack != null) {
                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
                    return;
                }
            }

            BlockState state = ctx.getParamOrNull(LootContextParams.BLOCK_STATE);

            Entity en = ctx.getParamOrNull(LootContextParams.THIS_ENTITY);

            var p = ctx.getParamOrNull(LootContextParams.ORIGIN);
            BlockPos pos = new BlockPos((int) p.x, (int) p.y, (int) p.z);

            Player player = null;
            if (en instanceof Player) {
                player = (Player) en;
            } else {
                return;
            }
            if (player.level().isClientSide) {
                return;
            }

            ExileEvents.PlayerMineFarmableBlockEvent event = new ExileEvents.PlayerMineFarmableBlockEvent(ci.getReturnValue(), state, player, pos);

            ExileEvents.PLAYER_MINE_FARMABLE.callEvents(event);

            if (!event.itemsToAddToDrop.isEmpty()) {
                ci.getReturnValue().addAll(event.itemsToAddToDrop);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
