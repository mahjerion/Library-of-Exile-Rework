package com.robertx22.library_of_exile.events;

import com.robertx22.library_of_exile.components.PlayerDataCapability;
import com.robertx22.library_of_exile.main.ApiForgeEvents;
import com.robertx22.library_of_exile.main.LibWords;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.util.UNICODE;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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

        // Driven from the END of the SERVER tick, deliberately not from PlayerTickEvent.
        //
        // PlayerTickEvent fires from inside ServerGamePacketListenerImpl.tick(), which does, in order:
        // resetPosition() (snapshots the player's position into firstGoodX/Y/Z), player.doTick() (the
        // player tick event), then absMoveTo(firstGoodX, firstGoodY, firstGoodZ). A cross-dimension
        // teleport executed from the player tick therefore gets its position immediately overwritten
        // with the snapshot - the OLD dimension's coordinates, now applied inside the NEW dimension.
        // Forge's Entity.setPosRaw patch then does a blocking, generate-if-missing chunk load at that
        // spot: on a map exit that is the overworld being generated at the dungeon instance's
        // coordinates (2104, 664 ...), on entry it is the map dimension at the overworld home
        // coordinates. Measured at 5.5-6.6s of server-thread park per teleport in spark, with
        // NoiseBasedChunkGenerator busy on the workers in a session where nobody explored anything.
        // The client's teleport-accept packet puts the player back a tick later, so nothing visible
        // ever hinted at it. Preloading the real destination cannot help with a load at the wrong place.
        //
        // The connection tick runs before onPostServerTick, so firing from here means the next
        // resetPosition() already sees the new position and the re-apply is a no-op.
        ApiForgeEvents.registerForgeEvent(TickEvent.ServerTickEvent.class, event ->
        {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            for (ServerPlayer p : event.getServer().getPlayerList().getPlayers()) {
                if (!p.isAlive() || p.tickCount < 10) {
                    continue;
                }
                try {
                    var cap = PlayerDataCapability.get(p);
                    if (cap != null) {
                        var delayed = cap.delayedTeleportData;
                        if (delayed != null) {
                            delayed.tick(p);

                            // the destination can take a second or two to generate, and until it has the
                            // player is still standing where they pressed the button. say so, or a slow
                            // entry reads as nothing having happened.
                            //
                            // the map grace countdown can't cover this: it only runs for players already
                            // inside a map dimension, and this is the window before they get there.
                            if (delayed.shouldAnnounceWait()) {
                                p.connection.send(new ClientboundSetActionBarTextPacket(
                                        LibWords.LOADING_DESTINATION.get().withStyle(ChatFormatting.YELLOW)));
                            }
                        }
                    }
                } catch (Exception e) {
                    // null check, because this runs every tick and the capability is genuinely absent for
                    // part of a normal player's life - it is invalidated between death and respawn. An NPE
                    // thrown from inside the handler that exists to swallow errors would escape into the
                    // event bus on every one of those ticks.
                    var cap = PlayerDataCapability.get(p);
                    if (cap != null) {
                        cap.delayedTeleportData = null;
                    }
                    e.printStackTrace();
                }
            }
        });
    }
}
