package com.robertx22.library_of_exile.dimension.device;

import com.robertx22.library_of_exile.database.relic.stat.RelicStatsContainer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * The contract every "start an instance" block entity (dungeon map device, harvest block, obelisk block)
 * exposes to the shared map device GUI.
 * <p>
 * The GUI, its packets and the item-moving rules live in the main mod, because it is the only mod that can
 * see both this interface and the relic item data (which is dungeon_realm's). The block entities only own
 * a small inventory and know how to start or join their own league's instance.
 * <p>
 * Inventory layout: slot {@link #MAP_SLOT} holds the map item, slots {@link #RELIC_SLOT_START} to
 * {@code RELIC_SLOT_START + RELIC_SLOTS - 1} hold relics. The GUI never grows past this, which is the hard
 * cap of 4 relics per device.
 */
public interface IMapDeviceBlockEntity {

    int MAP_SLOT = 0;
    int RELIC_SLOT_START = 1;
    int RELIC_SLOTS = 4;
    int SIZE = RELIC_SLOT_START + RELIC_SLOTS;

    SimpleContainer getDeviceInventory();

    MapDeviceKind getDeviceKind();

    /**
     * Whether this device is bound to a live instance that can be joined. SERVER ONLY: the implementations
     * compare against server-side world capabilities.
     */
    boolean isActivated();

    /**
     * Whether the map slot is shown at all. The harvest and obelisk blocks placed inside a dungeon map hand
     * out one free run and take no map item, so they hide it.
     */
    boolean hasMapSlot(Level level);

    /**
     * Whether this stack is a map this device can start. Must be a pure item check (no level access), the
     * client filters its picker with it.
     */
    boolean acceptsMapItem(ItemStack stack);

    /**
     * True when the device can start a run without a map item (the free in-map harvest/obelisk run, once).
     */
    boolean isFreeRunAvailable(Level level);

    /**
     * Start a new instance from the map in {@link #MAP_SLOT} (or the free run). The relic stats are supplied
     * lazily and must only be resolved at the point the run is actually created, so that a refused start
     * (level gate, cooldown, tickets...) never consumes relic uses. The supplier may return null when no
     * relics are slotted.
     *
     * @return whether a run was started
     */
    boolean startMap(Player player, Supplier<RelicStatsContainer> relicStats);

    /**
     * Join the instance this device is bound to. Only meaningful when {@link #isActivated()}.
     *
     * @return whether the join went through
     */
    boolean joinMap(Player player);
}
