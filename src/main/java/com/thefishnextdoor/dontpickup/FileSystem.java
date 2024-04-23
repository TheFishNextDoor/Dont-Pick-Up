package com.thefishnextdoor.dontpickup;

import java.io.File;
import java.util.UUID;

public class FileSystem {

    private static File dataFolder;

    public static void setup(DontPickUp plugin) {
        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        dataFolder = new File(pluginFolder, "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public static File getDataFolder() {
        return dataFolder;
    }

    public static File getPlayerFile(UUID id) {
        return new File(dataFolder, id.toString() + ".yml");
    }
}
