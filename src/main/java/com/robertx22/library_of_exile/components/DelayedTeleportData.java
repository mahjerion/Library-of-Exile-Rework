package com.robertx22.library_of_exile.components;

import com.robertx22.library_of_exile.dimension.structure.MapChunkPreloader;
import com.robertx22.library_of_exile.dimension.teleport.SavedTeleportPos;
import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.utils.CommandUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;

public class DelayedTeleportData {
    public String command = "";
    // delaying the teleport might or might not help, don't know
    public int ticks = 0;
    public SavedTeleportPos tp = new SavedTeleportPos();

    /**
     * How long to keep waiting for the destination chunks before giving up and doing it the old,
     * blocking way. A player must never be left standing where they pressed the button, so the
     * worst case here is exactly the behaviour this class used to have unconditionally.
     */
    private static final int MAX_WAIT_TICKS = 100;

    /**
     * Whether arriving should stamp {@code lastMapEnterTime}, which is what MapEntryGrace measures
     * the spawn grace from. The decision belongs to SavedPlayerMapTeleports.teleportToMap (only a
     * genuine entry counts, not an arena pad inside the same dimension) but the stamp itself has
     * to happen on arrival: the wait below can be seconds long, and stamping before it would let
     * the grace expire while the player is still standing in the dimension they came from - which
     * is precisely the swarm-on-arrival the grace exists to stop.
     */
    public boolean stampMapEnterOnArrival = false;

    /**
     * Optional work that must happen at the destination, once its chunks are loaded. Exists so a
     * caller can stop doing that work inline: anything touching the destination before the player
     * gets there is touching a chunk that is still generating, and blocks the server thread for
     * exactly as long as the teleport would have.
     */
    public Runnable onArrival = null;

    private boolean requested = false;
    private int waited = 0;

    public DelayedTeleportData(String command, int ticks, SavedTeleportPos tp) {
        this.command = command;
        this.ticks = ticks;
        this.tp = tp;
    }

    /** True while the destination is being brought up, so the caller can say so on the action bar. */
    public boolean isWaitingForChunks() {
        return !command.isEmpty() && requested;
    }

    /**
     * Ticks on which the "still loading" message should be sent: immediately, so a slow entry
     * never looks like a dead button, then once a second because the action bar fades on its own.
     */
    public boolean shouldAnnounceWait() {
        return isWaitingForChunks() && (waited <= 1 || waited % 20 == 0);
    }

    /**
     * Drives one tick of the teleport. Runs down the original short delay, then asks the chunk
     * pipeline for the destination and waits - without blocking - until it is ready.
     */
    public void tick(Player p) {
        try {
            if (command.isEmpty()) {
                return;
            }
            // unchanged from the original: the first couple of ticks are just a delay
            if (ticks-- >= 1) {
                return;
            }

            var key = ResourceKey.create(Registries.DIMENSION, tp.getDimensionId());
            ServerLevel level = p.getServer().getLevel(key);
            if (level == null) {
                // nothing to preload against, and refusing to teleport would strand the player
                teleport(p, null, false);
                return;
            }

            ChunkPos center = MapChunkPreloader.centerOf(tp.getPos());

            if (!requested) {
                requested = true;
                MapChunkPreloader.request(level, center, MapChunkPreloader.RADIUS);
            }

            if (MapChunkPreloader.isReady(level, center, MapChunkPreloader.RADIUS)) {
                teleport(p, level, false);
                return;
            }

            waited++;
            if (waited > MAX_WAIT_TICKS) {
                teleport(p, level, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            p.sendSystemMessage(Component.literal("Teleport failed, check log for error."));
            this.command = "";
        }
    }

    private void teleport(Player p, ServerLevel level, boolean gaveUpWaiting) {

        try {
            if (gaveUpWaiting && level != null) {
                // the pipeline never caught up. this is the old behaviour - a blocking, generate if
                // missing load on the server thread - kept only so a player can't be stranded. it
                // firing at all means chunk generation is badly backed up, which is worth knowing.
                ExileLog.get().warn("Gave up waiting for the chunks at " + tp.getPos() + " in "
                        + tp.getDimensionId() + " after " + MAX_WAIT_TICKS + " ticks, so "
                        + p.getScoreboardName() + "'s teleport is loading them the blocking way."
                        + " Chunk generation is not keeping up with how fast players are entering maps.");
                level.getChunk(tp.getPos());
            }

            CommandUtils.execute(p, command);
            this.command = "";

            // after the command, so p.level() is the destination - the same level MapEntryGrace
            // reads the game time from when it decides whether the window is still open
            if (stampMapEnterOnArrival) {
                // the capability is invalidated between death and respawn, and this runs off the player
                // tick. Losing the stamp only costs the arriving player their spawn grace; throwing here
                // would abort the arrival work below as well.
                var cap = PlayerDataCapability.get(p);
                if (cap != null && cap.mapTeleports != null) {
                    cap.mapTeleports.lastMapEnterTime = p.level().getGameTime();
                }
            }

            if (onArrival != null) {
                Runnable run = onArrival;
                onArrival = null;
                run.run();
            }

        } catch (Exception e) {
            e.printStackTrace();
            p.sendSystemMessage(Component.literal("Teleport failed, check log for error."));
            this.command = "";
        }

    }
}
