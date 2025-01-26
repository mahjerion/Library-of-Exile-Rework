package com.robertx22.library_of_exile.localization;

import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.registry.ExileRegistryContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ExileLangFile {

    public static HashMap<String, HashMap<String, ExileTranslation>> all = new HashMap<>();

    private static void gatherAll(String modid) {

        List<ITranslated> gathered = new ArrayList<>();

        for (Item item : ForgeRegistries.ITEMS) {
            if (item instanceof ITranslated tra) {
                gathered.add(tra);
            }
        }
        for (Block item : ForgeRegistries.BLOCKS) {
            if (item instanceof ITranslated tra) {
                gathered.add(tra);
            }
        }
        for (MobEffect item : ForgeRegistries.MOB_EFFECTS) {
            if (item instanceof ITranslated tra) {
                gathered.add(tra);
            }
        }
        for (ExileRegistryContainer<?> reg : Database.getAllRegistries()) {
            for (ExileRegistry o : reg.getAllIncludingSeriazable()) {
                if (o instanceof ITranslated tra) {
                    gathered.add(tra);
                }
            }
        }
        for (ITranslated tra : gathered) {
            var b = tra.createTranslationBuilder();
            if (b.modid.equals(modid)) {
                b.build();
            }
        }
    }


    public static String createJsonForModid(String modid, String extraManual) {

        gatherAll(modid);

        String json = "{\n";

        json += extraManual;

        var sorted = all.get(modid).values().stream().sorted(Comparator.comparing(x -> x.key)).collect(Collectors.toList());

        for (ExileTranslation tra : sorted) {
            json += "\t" + "\"" + tra.key + "\": \"" + StringEscapeUtils.escapeJava(tra.locname) + "\",\n";
        }

        json += "\n}";

        json = replaceLast(json, ",", ""); // removes last , or else json wont work


        return json;
    }

    public static String replaceLast(String string, String toReplace,
                                     String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos) + replacement + string.substring(pos + toReplace
                    .length(), string.length());
        } else {
            return string;
        }
    }


    public static void createFile(String modid, String extraManual) {
        try {

            var json = createJsonForModid(modid, extraManual);

            var path = langFilePath(modid);

            if (Files.exists(Paths.get(path)) == false) {
                Files.createFile(Paths.get(path));
            }

            File file = new File(path);

            FileWriter fw = new FileWriter(file);
            fw.write(json);
            fw.close();
            //ExileLog.get().log("Saved lang file to " + file.toPath()                    .toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static String modDir() {
        return FMLPaths.GAMEDIR.get()
                .toString()
                .replace("run", "src");
    }

    static String langFolderDir(String modid) {
        return modDir() + "/main/resources/assets/" + modid + "/lang/";
    }

    static String langFilePath(String modid) {
        return langFolderDir(modid) + "en_us.json";
    }
}
