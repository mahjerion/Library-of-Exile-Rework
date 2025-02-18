package com.robertx22.orbs_of_crafting.register;

import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;
import com.robertx22.orbs_of_crafting.register.orb_edit.OrbEdit;

public class OrbEdits extends ExileKeyHolder<OrbEdit> {

    public static OrbEdits INSTANCE = new OrbEdits(Ref.REGISTER_INFO);

    public OrbEdits(ModRequiredRegisterInfo modRegisterInfo) {
        super(modRegisterInfo);
    }

    public ExileKey<OrbEdit, KeyInfo> EMPTY = ExileKey.ofId(this, "empty", x -> OrbEdit.of(x.GUID(), ""));

    @Override
    public void loadClass() {

    }
}
