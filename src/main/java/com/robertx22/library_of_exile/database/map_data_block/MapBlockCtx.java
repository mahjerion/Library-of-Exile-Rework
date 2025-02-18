package com.robertx22.library_of_exile.database.map_data_block;

import com.robertx22.library_of_exile.components.LibMapData;


// just so i dont call it too often, hm maybe a bad idea..
// if theres many blocks in a chunk that use lib data, then this is more performant, but if there's none, it isn't..
public class MapBlockCtx {

    public LibMapData libMapData = new LibMapData();
}
