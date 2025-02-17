package com.robertx22.library_of_exile.database.relic.relic_type;

import com.robertx22.library_of_exile.database.map_data_block.MapDataBlock;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;

public class LibRelicTypes extends ExileKeyHolder<MapDataBlock> {

    public static LibRelicTypes INSTANCE = new LibRelicTypes(Ref.REGISTER_INFO);

    public LibRelicTypes(ModRequiredRegisterInfo modRegisterInfo) {
        super(modRegisterInfo);
    }


    public ExileKey<RelicType, KeyInfo> EMPTY = ExileKey.ofId(this, "empty", x -> new EmptyRelicType(x.GUID()));

    @Override
    public void loadClass() {

    }
}
