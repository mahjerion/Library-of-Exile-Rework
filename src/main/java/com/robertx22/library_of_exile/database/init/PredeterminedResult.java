package com.robertx22.library_of_exile.database.init;

import com.robertx22.library_of_exile.dimension.MapDimensions;
import com.robertx22.library_of_exile.dimension.MapGenerationUTIL;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Random;

// for stuff like what mob pool to pick from. Most maps will just be based on pos in the map world, but sometimes
// i might create a map that specifically wants certain mob spawns, in that case the event can be used to override the result
public interface PredeterminedResult<T> {

    public ExileRegistryType getRegistryType();

    public T getPredeterminedRandomINTERNAL(Random random, Level level, ChunkPos pos);

    public default T getPredeterminedRandomINTERNAL(Level level, BlockPos pos) {
        var event = new PredeterminedRandomEvent(getRegistryType(), level, pos);
        ExileEvents.PREDETERMINED_RANDOM.callEvents(event);

        if (!event.result.isEmpty()) {
            if (!Database.getRegistry(getRegistryType()).isRegistered(event.result)) {
                throw new RuntimeException(event.result + " is not registered! Can't use it for predetermined outcome override");
            }
            return (T) Database.getRegistry(getRegistryType()).get(event.result);
        }

        var start = MapDimensions.getInfo(level).structure.getStartChunkPos(pos);
        var random = MapGenerationUTIL.createRandom(start);
        return getPredeterminedRandomINTERNAL(random, level, start);
    }

}
