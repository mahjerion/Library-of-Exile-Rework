package com.robertx22.library_of_exile.database.league;

import com.robertx22.library_of_exile.main.Ref;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class EmptyLeague extends League {
    public EmptyLeague(String id) {
        super(id);
    }

    @Override
    public boolean isInSide(ServerLevel level, BlockPos pos) {
        return false;
    }

    @Override
    public ChatFormatting getTextColor() {
        return ChatFormatting.WHITE;
    }

    @Override
    public String modid() {
        return Ref.MODID;
    }

    @Override
    public String locName() {
        return "Empty/Error League";
    }


}
