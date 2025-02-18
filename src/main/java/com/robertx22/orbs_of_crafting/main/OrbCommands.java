package com.robertx22.orbs_of_crafting.main;

import com.robertx22.library_of_exile.command_wrapper.CommandBuilder;
import com.robertx22.library_of_exile.command_wrapper.PermWrapper;
import com.robertx22.library_of_exile.command_wrapper.PlayerWrapper;
import com.robertx22.library_of_exile.command_wrapper.RegistryWrapper;
import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.main.ApiForgeEvents;
import com.robertx22.orbs_of_crafting.misc.LocReqContext;
import com.robertx22.orbs_of_crafting.misc.StackHolder;
import com.robertx22.orbs_of_crafting.register.ExileCurrency;
import com.robertx22.orbs_of_crafting.register.mods.base.ItemModification;
import com.robertx22.orbs_of_crafting.register.mods.base.ItemModificationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;

public class OrbCommands {

    public static String ID = "orbs_of_crafting";

    public static void init() {

        ApiForgeEvents.registerForgeEvent(RegisterCommandsEvent.class, event -> {
            var dis = event.getDispatcher();

            CommandBuilder.of(ID, dis, x -> {
                PlayerWrapper PLAYER = new PlayerWrapper();
                RegistryWrapper<ExileCurrency> CURRENCY = new RegistryWrapper(LibDatabase.CURRENCY);

                x.addLiteral("orb", PermWrapper.OP);
                x.addLiteral("use", PermWrapper.OP);
                x.addArg(PLAYER);
                x.addArg(CURRENCY);

                x.action(e -> {
                    var p = PLAYER.get(e);
                    var mod = CURRENCY.getFromRegistry(e);
                    ItemStack stack = p.getMainHandItem();

                    var ctx = new LocReqContext(p, stack, mod.getItem().getDefaultInstance());

                    var ex = mod.canItemBeModified(ctx);
                    if (ex.can) {
                        var res = mod.modifyItem(ctx);
                        p.setItemSlot(EquipmentSlot.MAINHAND, res.stack.copy());

                    } else {
                        p.sendSystemMessage(ex.answer);
                    }

                });

            }, "Tries to apply an item currency to the item in player's hand.");

            CommandBuilder.of(ID, dis, x -> {
                PlayerWrapper PLAYER = new PlayerWrapper();
                RegistryWrapper<ItemModification> MOD = new RegistryWrapper(LibDatabase.ITEM_MOD);

                x.addLiteral("item_mod", PermWrapper.OP);
                x.addLiteral("use", PermWrapper.OP);
                x.addArg(PLAYER);
                x.addArg(MOD);

                x.action(e -> {

                    var res = new ItemModificationResult();

                    var p = PLAYER.get(e);
                    var mod = MOD.getFromRegistry(e);
                    ItemStack stack = p.getMainHandItem();

                    var ex = new StackHolder(stack);
                    mod.applyMod(p, ex, res);

                    res.onFinish(p);

                    p.setItemSlot(EquipmentSlot.MAINHAND, ex.stack);

                    p.sendSystemMessage(Component.literal("Applied Item Modification from Command").withStyle(ChatFormatting.GREEN));
                });

            }, "Applies an item modification to the item in player's hand.");
        });

    }
}
