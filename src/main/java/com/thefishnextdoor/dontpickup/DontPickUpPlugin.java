package com.thefishnextdoor.dontpickup;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.thefishnextdoor.dontpickup.command.DPU;
import com.thefishnextdoor.dontpickup.config.Language;
import com.thefishnextdoor.dontpickup.event.Join;
import com.thefishnextdoor.dontpickup.event.PickUp;
import com.thefishnextdoor.dontpickup.player.PlayerProfileManager;
import com.thefishnextdoor.dontpickup.scheduler.AutoSave;

public class DontPickUpPlugin extends JavaPlugin {

    private static DontPickUpPlugin instance;

    private static Language language;

    public void onEnable() {
        instance = this;

        loadConfigs();

        registerCommand("dontpickup", new DPU());

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new Join(), this);
        pluginManager.registerEvents(new PickUp(), this);

        AutoSave.start();

        logInfo("Plugin enabled");
    }

    public void onDisable() {
        AutoSave.stop();
        PlayerProfileManager.saveAll();
        logInfo("Plugin disabled");
    }

    public static void loadConfigs() {
        language = new Language(instance);
    }

    public static DontPickUpPlugin getInstance() {
        return instance;
    }

    public static Language getLanguage() {
        return language;
    }

    public static void logInfo(String message) {
        getInstance().getLogger().info(message);
    }

    public static void logWarning(String message) {
        getInstance().getLogger().warning(message);
    }

    public static void logSevere(String message) {
        getInstance().getLogger().severe(message);
    }

    private boolean registerCommand(String commandName, CommandExecutor commandExecutor) {
        PluginCommand command = getCommand(commandName);
        if (command == null) {
            DontPickUpPlugin.logSevere("Command '" + commandName + "' not found in plugin.yml.");
            return false;
        }

        command.setExecutor(commandExecutor);

        if (commandExecutor instanceof TabCompleter) {
            command.setTabCompleter((TabCompleter) commandExecutor);
        }

        return true;
    }
}