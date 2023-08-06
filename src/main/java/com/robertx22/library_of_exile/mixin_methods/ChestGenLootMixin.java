package com.robertx22.library_of_exile.mixin_methods;

import com.robertx22.library_of_exile.events.base.ExileEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ChestGenLootMixin {

    public static void onLootGen(Container inventory, LootParams context) {

        try {
            if (context.hasParam(LootContextParams.THIS_ENTITY) &&
                    context.hasParam(LootContextParams.ORIGIN)
                    && context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof Player) {

                BlockEntity chest = null;
                var p = context.getParamOrNull(LootContextParams.ORIGIN);
                BlockPos pos = new BlockPos((int) p.x, (int) p.y, (int) p.z);

                Player player = (Player) context.getParamOrNull(LootContextParams.THIS_ENTITY);
                Level world = player.level();

                if (inventory instanceof BlockEntity) {
                    chest = (BlockEntity) inventory;
                }
                if (chest == null) {
                    chest = world.getBlockEntity(pos);
                }
                if (world == null) {
                    return;
                }

                if (chest instanceof RandomizableContainerBlockEntity) {
                    ExileEvents.ON_CHEST_LOOTED.callEvents(new ExileEvents.OnChestLooted(player, context, inventory, pos));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
