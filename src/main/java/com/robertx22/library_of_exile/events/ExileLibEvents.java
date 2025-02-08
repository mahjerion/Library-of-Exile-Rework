package com.robertx22.library_of_exile.events;

import com.robertx22.library_of_exile.components.PlayerDataCapability;
import com.robertx22.library_of_exile.main.ApiForgeEvents;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.util.UNICODE;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ExileLibEvents {

    public static void init() {

        ApiForgeEvents.registerForgeEvent(PlayerEvent.PlayerLoggedInEvent.class, event ->
        {
            Player player = event.getEntity();

            if (player.level().isClientSide) {
                return;
            }
            try {

                if (!JsonExileRegistry.NOT_LOADED_JSONS_MAP.isEmpty()) {
                    int count = 0;
                    String hovertext = "";
                    for (Map.Entry<ExileRegistryType, Set<ResourceLocation>> en : JsonExileRegistry.NOT_LOADED_JSONS_MAP.entrySet()) {
                        for (ResourceLocation s : en.getValue()) {
                            hovertext += en.getKey().id + ": " + s.toString() + "\n";
                            count++;
                        }
                    }

                    var hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(hovertext));

                    player.sendSystemMessage(Component.literal("Datapack Error: " + count + " Jsons errored while loading.").withStyle(
                            Style.EMPTY.withHoverEvent(hover)
                    ));

                }
                // idk if this one is ever called, but better be safe

                Set<String> modNames = new LinkedHashSet<>();

                if (!JsonExileRegistry.INVALID_JSONS_MAP.isEmpty()) {
                    int count = 0;

                    String hovertext = "";
                    for (Map.Entry<ExileRegistryType, Set<String>> en : JsonExileRegistry.INVALID_JSONS_MAP.entrySet()) {
                        for (String s : en.getValue()) {
                            hovertext += en.getKey().id + ": " + s + "\n";
                            count++;
                            modNames.add(en.getKey().getModName());
                        }
                    }

                    var hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(hovertext));

                    player.sendSystemMessage(Component.literal("[DATAPACK ERROR]: " + count + " Jsons were marked as wrong with automatic error checking. Datapacks of these mods are affected:").withStyle(
                            Style.EMPTY.withHoverEvent(hover).applyFormats(ChatFormatting.RED)
                    ));
                    for (String modName : modNames) {
                        player.sendSystemMessage(Component.literal(" - " + modName).withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
                    }
                }


                if (!JsonExileRegistry.INVALID_JSONS_MAP.isEmpty() || !JsonExileRegistry.NOT_LOADED_JSONS_MAP.isEmpty()) {

                    player.sendSystemMessage(Component.literal("Check the log file for more info.")
                            .withStyle(ChatFormatting.YELLOW));
                    player.sendSystemMessage(Component.literal("THIS MEANS YOUR DATAPACKS ARE LIKELY BROKEN AND MIGHT BUG IN-GAME UNLESS FIXED")
                            .withStyle(ChatFormatting.LIGHT_PURPLE));

                    player.sendSystemMessage(Component.literal(UNICODE.STAR + " " + "If you're playing a Modpack, updating these mods will result in errors. wait for the modpack to update.")
                            .withStyle(ChatFormatting.AQUA));
                    player.sendSystemMessage(Component.literal(UNICODE.STAR + " " + "If you made the datapacks yourself, use the Info from the log file to help you fix the jsons.")
                            .withStyle(ChatFormatting.AQUA));


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ApiForgeEvents.registerForgeEvent(TickEvent.PlayerTickEvent.class, event ->
        {
            Player p = event.player;

            if (p.level().isClientSide || event.phase != TickEvent.Phase.END) {
                return;
            }
            if (!p.isAlive() || p.tickCount < 10) {
                return;
            }
            try {
                var cap = PlayerDataCapability.get(p);
                if (cap != null) {
                    var delayed = PlayerDataCapability.get(p).delayedTeleportData;
                    if (delayed != null) {
                        if (!delayed.command.isEmpty()) {
                            if (delayed.ticks-- < 1) {
                                delayed.teleport(p);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                PlayerDataCapability.get(p).delayedTeleportData = null;
                e.printStackTrace();
            }
        });
    }
}
