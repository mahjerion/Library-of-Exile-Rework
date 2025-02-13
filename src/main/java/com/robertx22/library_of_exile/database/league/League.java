package com.robertx22.library_of_exile.database.league;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.dimension.MapDimensionInfo;
import com.robertx22.library_of_exile.dimension.structure.MapStructure;
import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.ITranslated;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.localization.TranslationType;
import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.util.UNICODE;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;


// A league is i think only needed for mns
// some things arent structures, like prophecy, instead i need some tag or similar thing to define something,
// so prophecy league can have exclusive uniques for example
public abstract class League implements ExileRegistry<League>, ITranslated {


    public String id;

    public League(String id) {
        this.id = id;
    }

    public League getFromPosition(ServerLevel level, BlockPos pos) {
        return LibDatabase.Leagues().getList().stream().filter(x -> x.isInSide(level, pos)).findAny().orElse(LibLeagues.INSTANCE.EMPTY.get());
    }

    public abstract boolean isInSide(ServerLevel level, BlockPos pos);


    public abstract ChatFormatting getTextColor();

    public abstract String modid();

    public abstract String locName();

    @Override
    public ExileRegistryType getExileRegistryType() {
        return LibDatabase.MAP_CONTENT;
    }

    @Override
    public int Weight() {
        return 1000;
    }

    @Override
    public String GUID() {
        return id;
    }

    public MutableComponent getPrettifiedName() {
        return Component.literal(UNICODE.SKULL + " ").withStyle(getTextColor())
                .append(getTranslation(TranslationType.NAME).getTranslatedName().withStyle(getTextColor()));
    }

    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(modid()).name(ExileTranslation.registry(this, locName()));
    }

    public static boolean structureLeagueCheck(MapDimensionInfo map, MapStructure struc, ServerLevel level, BlockPos pos) {
        return map.isInside(struc, level, pos);
    }

}
