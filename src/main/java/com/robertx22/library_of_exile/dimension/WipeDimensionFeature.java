package com.robertx22.library_of_exile.dimension;

import com.robertx22.library_of_exile.main.ExileLog;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class WipeDimensionFeature {


    public static void OnStartResetMap(MapDimensionInfo map, ResourceLocation mapid) {
        File file = new File(FMLPaths.GAMEDIR.get().toAbsolutePath().toString());
        var list = FileUtils.listFilesAndDirs(file, new Dir(), new Dir());

        for (File dir : list) {
            try {
                if (dirMatches(dir, mapid)) {
                    deleteDirectoryRecursion(dir.toPath());
                    map.markDataForClear = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    static class Dir implements IOFileFilter {

        @Override
        public boolean accept(File file) {
            return file != null && file.isDirectory();

        }

        @Override
        public boolean accept(File dir, String name) {
            return true;

        }
    }

    private static boolean dirMatches(File file, ResourceLocation mapid) {
        if (file.isDirectory()) {
            String str = file.toPath().toString();
            String check = "dimensions" + File.separator + mapid.getNamespace() + File.separator + mapid.getPath();

            if (str.endsWith(check)) {
                ExileLog.get().log("Wiping dimension folder: " + file.toString() + " - If this is a " + mapid.toString() + " dimension folder, then the feature is working correctly. Report if it tries deleting something else though!");
                ExileLog.get().log("These dimensions are meant to be wiped on server boot to remain infinite.");
                return true;
            }
        }
        return false;

    }

    static void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        Files.delete(path);

    }
}
