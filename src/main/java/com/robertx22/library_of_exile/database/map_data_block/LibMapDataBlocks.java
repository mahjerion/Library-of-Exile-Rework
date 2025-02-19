package com.robertx22.library_of_exile.database.map_data_block;

import com.robertx22.library_of_exile.database.map_data_block.all.EmptyMBlock;
import com.robertx22.library_of_exile.database.map_data_block.all.SetBlockMB;
import com.robertx22.library_of_exile.main.ExileLibEntries;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;

public class LibMapDataBlocks extends ExileKeyHolder<MapDataBlock> {

    public static LibMapDataBlocks INSTANCE = new LibMapDataBlocks(Ref.REGISTER_INFO);

    public LibMapDataBlocks(ModRequiredRegisterInfo modRegisterInfo) {
        super(modRegisterInfo);
    }

    // todo, maybe a marker position block? Like mark a pos where chests will spawn in the map data, so it can happen AFTER event is done?

    public ExileKey<MapDataBlock, KeyInfo> EMPTY = ExileKey.ofId(this, "empty", x -> new EmptyMBlock(x.GUID()));
    public ExileKey<MapDataBlock, KeyInfo> TELEPORT_BACK_BLOCK = ExileKey.ofId(this, "teleport_back_block", x -> new SetBlockMB(x.GUID(), ExileLibEntries.TELEPORT_BACK_BLOCK.getId().toString()));
    public ExileKey<MapDataBlock, KeyInfo> TP_BACK_LEAGUE_BACK_OLD = ExileKey.ofId(this, "league_back", x -> new SetBlockMB(x.GUID(), ExileLibEntries.TELEPORT_BACK_BLOCK.getId().toString()));
    // think not needeed? public ExileKey<MapDataBlock, KeyInfo> MAP_TELEPORTER = ExileKey.ofId(this, "map_teleporter", x -> new SetBlockMB(x.GUID(), ExileLibEntries.TELEPORT_BACK_BLOCK.getId().toString()));

    @Override
    public void loadClass() {

    }
}
