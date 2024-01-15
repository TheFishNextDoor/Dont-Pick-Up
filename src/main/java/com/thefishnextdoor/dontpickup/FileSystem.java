package com.thefishnextdoor.dontpickup;

import java.io.File;

public class FileSystem {

    private static File dataFolder;

    public static void setup(Plugin plugin) {
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
}
