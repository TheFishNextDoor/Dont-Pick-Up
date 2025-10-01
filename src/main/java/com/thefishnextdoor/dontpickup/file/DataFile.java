package com.thefishnextdoor.dontpickup.file;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.thefishnextdoor.dontpickup.DontPickUpPlugin;

public class DataFile {

    public static YamlConfiguration get(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }

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

    public static boolean save(String name, YamlConfiguration data) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

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
