package com.robertx22.library_of_exile.dimension.device;

/**
 * Which league a map device block starts. Decides the optional parts of the shared device GUI: only the
 * dungeon device has an Atlas and Entry Tickets, the harvest and obelisk ones are just a map + relics.
 */
public enum MapDeviceKind {
    DUNGEON(true, true),
    HARVEST(false, false),
    OBELISK(false, false);

    public final boolean showsAtlas;
    public final boolean showsEntryTickets;

    MapDeviceKind(boolean showsAtlas, boolean showsEntryTickets) {
        this.showsAtlas = showsAtlas;
        this.showsEntryTickets = showsEntryTickets;
    }
}
