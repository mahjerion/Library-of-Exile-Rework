package com.robertx22.library_of_exile.main;

import com.robertx22.library_of_exile.database.affix.types.ExileMobAffix;
import com.robertx22.library_of_exile.localization.ExileLangFile;
import com.robertx22.library_of_exile.localization.ITranslated;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LibDataGen implements DataProvider {

    public LibDataGen() {

    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {

        // translations
        List<ITranslated> tra = new ArrayList<>();
        tra.addAll(Arrays.stream(ExileMobAffix.Affects.values()).toList());
        tra.addAll(Arrays.stream(LibWords.values()).toList());

        for (ITranslated t : tra) {
            t.createTranslationBuilder().build();
        }
        ExileLangFile.createFile(Ref.MODID, "");

        for (ExileRegistryType type : ExileRegistryType.getAllInRegisterOrder()) {
            type.getDatapackGenerator().run(pOutput);
        }


        return CompletableFuture.completedFuture(null); // todo this is bad, but would it work?
    }


    @Override
    public String getName() {
        return "lib_data";
    }
}
