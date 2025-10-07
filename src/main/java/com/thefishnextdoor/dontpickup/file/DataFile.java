package com.thefishnextdoor.dontpickup.file;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.thefishnextdoor.dontpickup.DontPickUpPlugin;

public class DataFile {

    public static YamlConfiguration get(@NonNull String name) {
        File dataFile = new File(getFolder(), name + ".yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(dataFile);
    }

    public static boolean save(@NonNull String name, @NonNull YamlConfiguration data) {
        File dataFile = new File(getFolder(), name + ".yml");
        try {
            data.save(dataFile);
        } 
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static File getFolder() {
        File pluginFolder = DontPickUpPlugin.getInstance().getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        File dataFolder = new File(pluginFolder, "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        return dataFolder;
    }   
}