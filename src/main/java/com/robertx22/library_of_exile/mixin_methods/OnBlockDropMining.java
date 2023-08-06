package com.robertx22.library_of_exile.mixin_methods;

import com.robertx22.library_of_exile.events.base.ExileEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class OnBlockDropMining {

    public static void run(LootContext ctx, CallbackInfoReturnable<List<ItemStack>> ci) {

        try {
            if (!ctx.hasParam(LootContextParams.BLOCK_STATE)) {
                return;
            }
            if (!ctx.hasParam(LootContextParams.TOOL)) {
                return;
            }
            if (!ctx.hasParam(LootContextParams.ORIGIN)) {
                return;
            }
            if (!ctx.hasParam(LootContextParams.THIS_ENTITY)) {
                return;
            }

            ItemStack stack = ctx.getParamOrNull(LootContextParams.TOOL);
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
                return;
            }

            BlockState state = ctx.getParamOrNull(LootContextParams.BLOCK_STATE);
            Block block = state.getBlock();

            if (ci.getReturnValue()
                    .stream()
                    .anyMatch(x -> x.getItem() == block.asItem())) {
                return; // if a diamond ore is broken and drops diamond ore, don't give exp and loot
            }

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

            ExileEvents.PlayerMineOreEvent event = new ExileEvents.PlayerMineOreEvent(state, player, pos);

            ExileEvents.PLAYER_MINE_ORE.callEvents(event);

            if (!event.itemsToAddToDrop.isEmpty()) {
                ci.getReturnValue()
                        .addAll(event.itemsToAddToDrop);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}