package com.robertx22.library_of_exile.database.init;

import com.robertx22.library_of_exile.custom_ser.CustomSerializer;
import com.robertx22.library_of_exile.database.affix.types.ExileMobAffix;
import com.robertx22.library_of_exile.database.map_data_block.MapDataBlock;
import com.robertx22.library_of_exile.database.relic.stat.RelicStat;

public class ExileCustomSers {

    public static CustomSerializer<MapDataBlock> MAP_DATA_BLOCK = new CustomSerializer<>();
    public static CustomSerializer<RelicStat> ATLAS_STAT = new CustomSerializer<>();
    public static CustomSerializer<ExileMobAffix> MOB_AFFIX = new CustomSerializer<>();

}
