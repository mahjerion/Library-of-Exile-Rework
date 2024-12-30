package com.robertx22.library_of_exile.registry;

import com.robertx22.library_of_exile.registry.serialization.ISerializable;
import com.robertx22.library_of_exile.registry.util.ExileExtraDatas;
import com.robertx22.library_of_exile.registry.util.ExileRegistryUtil;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ExileDatapackGenerator<T extends IGUID & ISerializable<T>> extends BaseDatapackGenerator<T> implements DataProvider {
    String modid;


    public ExileDatapackGenerator(String modid, List<T> list, String category) {
        super(list, category);
        this.modid = modid;

    }


    public Path resolve(Path path, T object) {
        return path.resolve(
                "data/" + modid + "/" + category + "/" + object.datapackFolder() + object.getFileName()
                        .replaceAll(":", "_") +
                        ".json");
    }

    protected CompletableFuture<?> generateAll(CachedOutput cache) {

        try {
            Path path = gameDirPath();

            for (T entry : list) {
                
                if (entry instanceof ExileRegistry er) {
                    var data = ExileExtraDatas.MODID_OF_SERIAZABLE.get(er);
                    if (!ExileRegistryUtil.MODID_TO_GENERATE_DATA.test(data.getModid())) {
                        continue;
                    }
                }
                if (!entry.shouldGenerateJson()) {
                    continue;
                }

                Path target = movePath(resolve(path, entry));

                target = Paths.get(target.toString()
                        .replace("\\.\\", "\\"));

                try {
                    DataProvider.saveStable(cache, entry.toJson(), target);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(Optional.empty()); // todo..??
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return generateAll(pOutput);
    }

    @Override
    public String getName() {
        return this.category;
    }
}
