package com.robertx22.library_of_exile.unidentified;

import com.robertx22.library_of_exile.main.ApiForgeEvents;
import com.robertx22.library_of_exile.utils.SoundUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

public class IdentifiableItems {

    public static HashMap<ResourceLocation, Config> map = new HashMap<>();


    public static void register(ResourceLocation item, Config isUnidentified) {
        map.put(item, isUnidentified);
    }

    public abstract static class Config {

        public abstract boolean isUnidentified(ItemStack stack);

        public abstract void identify(Player p, ItemStack stack);

    }

    public static void init() {


        ApiForgeEvents.registerForgeEvent(PlayerInteractEvent.RightClickItem.class, event -> {
            try {
                ItemStack stack = event.getItemStack();

                if (event.getEntity() instanceof ServerPlayer p) {
                    var id = ForgeRegistries.ITEMS.getKey(stack.getItem());

                    if (map.containsKey(id)) {
                        var config = map.get(id);
                        if (config.isUnidentified(stack)) {
                            config.identify(p, stack);
                            SoundUtils.playSound(p, SoundEvents.EXPERIENCE_ORB_PICKUP);
                            event.setCancellationResult(InteractionResult.SUCCESS);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
