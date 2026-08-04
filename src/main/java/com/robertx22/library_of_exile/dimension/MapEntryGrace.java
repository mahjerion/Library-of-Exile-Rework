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
}
