package com.robertx22.library_of_exile.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RecipeGenerator {

    // the id is really just used to make sure this list isnt infinitely added upon with game reloads or something
    static HashMap<String, List<Supplier<RecipeBuilder>>> map = new HashMap<>();

    static HashMap<String, List<Supplier<ConditionalRecipeData>>> forgeConditional = new HashMap<>();


    public static void addConditional(String modid, Supplier<ConditionalRecipeData> sup) {
        if (!forgeConditional.containsKey(modid)) {
            forgeConditional.put(modid, new ArrayList<>());
        }
        forgeConditional.get(modid).add(sup);
    }


    public static void addRecipe(String modid, Supplier<RecipeBuilder> sup) {
        if (!map.containsKey(modid)) {
            map.put(modid, new ArrayList<>());
        }
        map.get(modid).add(sup);
    }

    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    public RecipeGenerator() {


    }

    protected static Path getBasePath() {
        return FMLPaths.GAMEDIR.get();
    }

    protected static Path movePath(Path target) {
        String movedpath = target.toString();
        movedpath = movedpath.replace("run", "src/generated/resources");
        return Paths.get(movedpath);
    }

    private static Path resolve(Path path, String modid, String id) {
        return path.resolve("data/" + modid + "/recipes/" + id + ".json");
    }


    public static void generateAll(CachedOutput cache, String modid) {

        Path path = getBasePath();

        generate(modid, x -> {
            Path target = movePath(resolve(path, modid, x.getId().getPath()));
            DataProvider.saveStable(cache, x.serializeRecipe(), target);
        });

        generateC(modid, x -> {
            Path target = movePath(resolve(path, modid, x.getId().getPath()));
            DataProvider.saveStable(cache, x.serializeRecipe(), target);
        });
    }

    private static void generateC(String modid, Consumer<FinishedRecipe> consumer) {
        if (forgeConditional.containsKey(modid)) {
            for (Supplier<ConditionalRecipeData> b : forgeConditional.get(modid)) {
                if (b != null && b.get() != null) {
                    b.get().builder.build(consumer, b.get().id);
                }
            }
        }
    }

    private static void generate(String modid, Consumer<FinishedRecipe> consumer) {
        if (!map.containsKey(modid)) {
            ExileLog.get().log(modid + " don't have any recipes need to be built!");
            return;
        }
        for (Supplier<RecipeBuilder> b : map.get(modid)) {
            if (b != null && b.get() != null) {
                b.get().save(consumer);
            }
        }
    }


}
