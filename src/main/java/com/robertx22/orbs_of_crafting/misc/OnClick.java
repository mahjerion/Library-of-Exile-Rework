package com.robertx22.orbs_of_crafting.misc;

import com.robertx22.library_of_exile.main.ApiForgeEvents;
import com.robertx22.library_of_exile.utils.SoundUtils;
import com.robertx22.orbs_of_crafting.register.ExileCurrency;
import com.robertx22.orbs_of_crafting.register.mods.base.ItemModification;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.ClickAction;
import net.minecraftforge.event.ItemStackedOnOtherEvent;

import java.util.ArrayList;
import java.util.List;

// todo

public class OnClick {
    static List<ClickFeature> CLICKS = new ArrayList<>();

    private static class Result {

        public boolean can;

        public Result(boolean can) {
            this.can = can;
        }

        private boolean doDing = false;

        public Result ding() {
            this.doDing = true;
            return this;
        }
    }

    private abstract static class ClickFeature {
        public abstract Result tryApply(ClickContext ctx);
    }

    public static void register() {    // new datapack currencies
        CLICKS.add(new ClickFeature() {
            @Override
            public Result tryApply(ClickContext ctx) {
                var opt = ExileCurrency.get(ctx.currency);

                if (opt.isEmpty()) {
                    return new Result(false);
                }
                if (ExileCurrency.get(ctx.target).isPresent()) {
                    // we don't want to ding the player when they try stacking 2 currencies
                    return new Result(false);
                }
                if (!ctx.isValid()) {
                    return new Result(false);
                }

                LocReqContext req = new LocReqContext(ctx.player, ctx.target.copy(), ctx.currency);

                var cur = opt.get();
                var can = cur.canItemBeModified(req);

                if (!can.can) {
                    SoundUtils.playSound(ctx.player.level(), ctx.player.blockPosition(), SoundEvents.VILLAGER_NO, 1, 1);
                    ctx.player.sendSystemMessage(can.answer);
                    return new Result(false);
                }

                var result = cur.modifyItem(req);

                if (result.resultEnum != ModifyResult.SUCCESS) {
                    ctx.player.sendSystemMessage(result.result.answer);
                    return new Result(false);
                }

                ctx.consumeCurrency(1);
                ctx.replaceTarget(result.stack.copy()); // the currency builds a new item, so the old one goes away

                if (result.outcome == ItemModification.OutcomeType.BAD) {
                    SoundUtils.playSound(ctx.player.level(), ctx.player.blockPosition(), SoundEvents.GLASS_BREAK, 1, 1);
                    return new Result(true);
                } else if (result.outcome == ItemModification.OutcomeType.NEUTRAL) {
                    SoundUtils.playSound(ctx.player.level(), ctx.player.blockPosition(), SoundEvents.STONE_PLACE, 1, 1);
                    return new Result(true);
                } else {
                    return new Result(true).ding();
                }
            }
        });

        ApiForgeEvents.registerForgeEvent(ItemStackedOnOtherEvent.class, x -> {
            var player = x.getPlayer();

            if (player.level().isClientSide) {
                return;
            }
            if (x.getClickAction() != ClickAction.SECONDARY) {
                // return;
            }

            ClickContext ctx = ClickContext.of(x);

            for (ClickFeature click : CLICKS) {
                var result = click.tryApply(ctx);

                if (result.doDing) {
                    SoundUtils.ding(player.level(), player.blockPosition());
                    SoundUtils.playSound(player.level(), player.blockPosition(), SoundEvents.ANVIL_USE, 1, 1);
                }

                if (result.can) {
                    x.setCanceled(true);
                    break;
                }
            }
        });

    }
}
