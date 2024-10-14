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
            if (context.hasParam(LootContextParams.THIS_ENTITY)
                    && context.hasParam(LootContextParams.ORIGIN) && context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof Player player) {

                BlockEntity chest = null;
                var p = context.getParamOrNull(LootContextParams.ORIGIN);
                BlockPos pos = new BlockPos((int) p.x, (int) p.y, (int) p.z);


                Level world = player.level();

                if (world == null) {
                    return;
                }
                if (inventory instanceof BlockEntity) {
                    chest = (BlockEntity) inventory;
                }
                if (chest == null) {
                    chest = world.getBlockEntity(pos);

                    if (chest == null) {
                        // this fixes lootr incompatibility because they offset the position with a center, but maybe abusable?
                        int radius = 1;
                        for (int x = -radius; x < radius; x++) {
                            for (int y = -radius; y < radius; y++) {
                                for (int z = -radius; z < radius; z++) {
                                    if (chest == null) {
                                        chest = world.getBlockEntity(pos.offset(x, y, z));
                                    }
                                }
                            }
                        }
                    }
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
