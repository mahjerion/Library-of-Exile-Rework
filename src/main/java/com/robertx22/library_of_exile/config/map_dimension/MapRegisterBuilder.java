package com.robertx22.library_of_exile.config.map_dimension;

import com.robertx22.library_of_exile.dimension.MapChunkGenEvent;
import com.robertx22.library_of_exile.dimension.MapChunkGens;
import com.robertx22.library_of_exile.dimension.MapDimensionInfo;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.events.base.EventConsumer;
import net.minecraft.resources.ResourceLocation;

public class MapRegisterBuilder {

    public MapDimensionInfo info;

    public MapRegisterBuilder(MapDimensionInfo info) {
        this.info = info;
    }

    public MapRegisterBuilder chunkGenerator(EventConsumer<MapChunkGenEvent> event, ResourceLocation chunkGenId) {
        MapChunkGens.registerMapChunkGenerator(chunkGenId, event);
        return this;
    }

    public void build() {
        MapDimensions.register(info);
    }
}
