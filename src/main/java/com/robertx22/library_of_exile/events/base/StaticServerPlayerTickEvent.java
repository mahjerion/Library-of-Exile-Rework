package com.robertx22.library_of_exile.events.base;

import com.robertx22.library_of_exile.utils.EntityUtils;
import com.robertx22.library_of_exile.utils.TeleportUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = "mmorpg")
public class StaticServerPlayerTickEvent {

    public static HashMap<UUID, PlayerTickData> PlayerTickDatas = new HashMap<UUID, PlayerTickData>();

    @SubscribeEvent
    public void onEndTick(TickEvent.ServerTickEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        for (ServerPlayer player : server.getPlayerList()
                .getPlayers()) {

            try {


                PlayerTickData data = PlayerTickDatas.get(player.getUUID());

                if (data == null) {
                    data = new PlayerTickData();
                }

                if (data.ensureTeleportData == null) {
                    if (data.removeInvlunTicks > 0) {
                        player.setInvulnerable(false);
                    }
                } else {

                    data.tick();

                    if (data.ensureTeleportData.ticks > 3) {
                        data.removeInvlunTicks = 200;
                        data.ensureTeleportData.action.accept(data.ensureTeleportData);
                        if (data.ensureTeleportData.ticksLeft < 1) {
                            data.ensureTeleportData = null;
                        }
                    }
                }

                PlayerTickDatas.put(player.getUUID(), data);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static Consumer<EnsureTeleportData> MAKE_SURE_TELEPORT = x -> {

        if (x.player.isDeadOrDying()) {
            x.cancel();
            return;
        }

        x.player.setInvulnerable(true);

        if (x.player.blockPosition()
                .distSqr(x.whereShouldTeleport) > 1000) {

            if (x.tries > 3) {
                BlockPos spawnpos = x.player.getRespawnPosition();
                if (spawnpos != null) {
                    EntityUtils.setLoc(x.player, spawnpos);
                }
                x.cancel(); // pray that works at least
                return;
            } else {

                x.resetTicks();

                x.tries++;

                x.player.displayClientMessage(Component.literal("There was a teleport bug but the auto correction system should have teleported you back correctly"), false);

                TeleportUtils.teleport(x.player, x.whereShouldTeleport);
            }
        }

    };

    public static void makeSureTeleport(ServerPlayer player, BlockPos teleportPos, ResourceLocation dim) {

        if (!PlayerTickDatas.containsKey(player.getUUID())) {
            PlayerTickDatas.put(player.getUUID(), new PlayerTickData());
        }
        player.setInvulnerable(true);
        PlayerTickDatas.get(player.getUUID()).ensureTeleportData =
                new EnsureTeleportData(player, MAKE_SURE_TELEPORT, 50, teleportPos, dim);
    }

    public static class PlayerTickData {
        public EnsureTeleportData ensureTeleportData;
        int removeInvlunTicks = 0;

        public void tick() {
            if (ensureTeleportData != null) {
                ensureTeleportData.ticksLeft--;
                ensureTeleportData.ticks++;
            }
        }
    }

}
