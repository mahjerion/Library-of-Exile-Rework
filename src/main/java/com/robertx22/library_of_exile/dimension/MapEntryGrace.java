package com.robertx22.library_of_exile.dimension;

import com.robertx22.library_of_exile.components.PlayerDataCapability;
import com.robertx22.library_of_exile.config.map_dimension.MapDimensionConfig;
import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    /**
     * Returned by {@link #enteredMapAt} when the player has no readable stamp. Deliberately a value
     * no real stamp can be: {@code lastMapEnterTime} is a game time, so it is never negative, and
     * both callers below already treat a negative delta as expired.
     */
    private static final long NO_STAMP = Long.MIN_VALUE;

    /**
     * The player's last map entry stamp, or {@link #NO_STAMP} when there isn't one to read.
     * <p>
     * The capability is genuinely absent for part of a normal player's life: it is invalidated
     * between death and respawn while the entity keeps ticking, so {@code PlayerDataCapability.get}
     * returns null on every tick the death screen is up. Dereferencing it there threw twice per tick
     * per dead player - 694 stack traces in 25 seconds on the live server, which through
     * {@code printStackTrace} -> System.err -> log4j became ~16,000 synchronous log events on the
     * server thread. The answer was always "not in grace" anyway; this just reaches it without the
     * exception.
     */
    private static long enteredMapAt(Player p) {
        var cap = PlayerDataCapability.get(p);
        if (cap == null || cap.mapTeleports == null) {
            return NO_STAMP;
        }
        return cap.mapTeleports.lastMapEnterTime;
    }

    public static boolean isInGrace(Player p, MapDimensionConfig config) {
        try {
            int seconds = config.SPAWN_GRACE_SECONDS.get();
            if (seconds < 1) {
                return false;
            }
            long stamp = enteredMapAt(p);
            if (stamp == NO_STAMP) {
                return false;
            }
            long since = p.level().getGameTime() - stamp;
            // a negative delta means the stamp is from a world whose game time has since gone backwards
            // (restored backup, /time set); treat it as expired rather than as a permanent grace
            return since >= 0 && since < seconds * 20L;
        } catch (Exception e) {
            // never let this stop content from spawning
            reportOnce("deciding whether a player is in the map entry grace", e);
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
            long stamp = enteredMapAt(p);
            if (stamp == NO_STAMP) {
                return 0;
            }
            long since = p.level().getGameTime() - stamp;
            long remaining = seconds * 20L - since;
            // remaining > seconds * 20 means since went negative, ie the stamp is from a world whose game
            // time has since gone backwards - isInGrace treats that as expired, so this must report 0 too
            if (remaining <= 0 || since < 0) {
                return 0;
            }
            return (int) Math.ceil(remaining / 20.0);
        } catch (Exception e) {
            // a display helper must never be the thing that breaks a map
            reportOnce("counting down the map entry grace", e);
            return 0;
        }
    }

    // Both methods above run once per player per tick, so anything that throws here throws tens of
    // times a second for as long as the cause lasts. The null capability that used to do exactly that
    // is handled properly now, but the catch blocks stay as backstops - and a backstop that can flood
    // the log is worse than the bug it was catching. One report per cause, then silence.
    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();

    private static void reportOnce(String doingWhat, Exception e) {
        // keyed by cause rather than by call site: a second, different failure still gets reported
        String key = doingWhat + "|" + e.getClass().getName() + "|" + e.getMessage();
        if (REPORTED.size() < 64 && REPORTED.add(key)) {
            ExileLog.get().error("Failed while " + doingWhat
                    + ". Content spawns as if the grace had expired. Further identical reports are suppressed.", e);
        }
    }
}
