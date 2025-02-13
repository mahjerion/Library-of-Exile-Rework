package com.robertx22.library_of_exile.dimension;

public enum MapContentType {
    PRIMARY_CONTENT(true), // adventure maps, primary content can spawn other side content
    SIDE_CONTENT(false), // stuff like obelisks, harvest, etc
    SECONDARY_CONTENT(false), // stuff that spawns in the same dimension, like boss arena and reward room
    EMPTY(false);

    public boolean canSpawnSideContent = false;

    MapContentType(boolean canSpawnSideContent) {
        this.canSpawnSideContent = canSpawnSideContent;
    }
}
