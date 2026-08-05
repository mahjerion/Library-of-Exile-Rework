package com.robertx22.library_of_exile.dimension;

import com.robertx22.library_of_exile.components.PlayerDataCapability;
import com.robertx22.library_of_exile.config.map_dimension.MapDimensionConfig;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * A short window after a player is teleported into a map dimension during which no content spawns around
 * them.
 * <p>
 * The server puts a player in the target level immediately, but their client can need several seconds to
 * receive and render those chunks - on low end hardware long enough to be killed by a wave that started
 * the tick they arrived. Nothing used to wait: the chunk processor runs off {@code p.tickCount}, which is
 * lifetime ticks and so is already past its guard on arrival, and the harvest/obelisk wave logic only
 * asked whether anyone was in the instance at all.
 * <p>
 * Length is per dimension ({@link MapDimensionConfig} SPAWN_GRACE_SECONDS); 0 restores the old behaviour.
 */
public class MapEntryGrace {

    public static boolean isInGrace(Player p, MapDimensionConfig config) {
        try {
            int seconds = config.SPAWN_GRACE_SECONDS.get();
            if (seconds < 1) {
                return false;
            }
            long since = p.level().getGameTime() - PlayerDataCapability.get(p).mapTeleports.lastMapEnterTime;
            // a negative delta means the stamp is from a world whose game time has since gone backwards
            // (restored backup, /time set); treat it as expired rather than as a permanent grace
            return since >= 0 && since < seconds * 20L;
        } catch (Exception e) {
            // never let this stop content from spawning
            e.printStackTrace();
            return false;
        }
    }

    // spawning is per instance, not per player, so one player still loading holds the whole room back
    public static boolean anyInGrace(List<Player> players, MapDimensionConfig config) {
        for (Player p : players) {
            if (isInGrace(p, config)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whole seconds still to wait before content starts, rounded up; 0 once the window is over or when
     * it's disabled. Lives next to {@link #isInGrace} and reads the same two numbers on purpose - a
     * countdown derived separately could tell the player "0" while spawning is still held, or "1" after
     * it already resumed.
     */
    public static int secondsLeft(Player p, MapDimensionConfig config) {
        try {
            int seconds = config.SPAWN_GRACE_SECONDS.get();
            if (seconds < 1) {
                return 0;
            }
            long since = p.level().getGameTime() - PlayerDataCapability.get(p).mapTeleports.lastMapEnterTime;
            long remaining = seconds * 20L - since;
            // remaining > seconds * 20 means since went negative, ie the stamp is from a world whose game
            // time has since gone backwards - isInGrace treats that as expired, so this must report 0 too
            if (remaining <= 0 || since < 0) {
                return 0;
            }
            return (int) Math.ceil(remaining / 20.0);
        } catch (Exception e) {
            // a display helper must never be the thing that breaks a map
            e.printStackTrace();
            return 0;
        }
    }
}
