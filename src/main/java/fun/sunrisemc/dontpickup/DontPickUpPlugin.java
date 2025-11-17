package fun.sunrisemc.dontpickup;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.sunrisemc.dontpickup.command.DontPickUpCommand;
import fun.sunrisemc.dontpickup.config.Language;
import fun.sunrisemc.dontpickup.event.PlayerJoin;
import fun.sunrisemc.dontpickup.event.PickupItem;
import fun.sunrisemc.dontpickup.player.PlayerProfileManager;
import fun.sunrisemc.dontpickup.scheduler.AutoSave;

public class DontPickUpPlugin extends JavaPlugin {

    private static @Nullable DontPickUpPlugin instance;

    private static @Nullable Language language;

    // Java Plugin

    public void onEnable() {
        instance = this;

        language = new Language();

        registerCommand("dontpickup", new DontPickUpCommand());

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoin(), this);
        pluginManager.registerEvents(new PickupItem(), this);

        AutoSave.start();

        logInfo("Plugin enabled");
    }

    public void onDisable() {
        AutoSave.stop();
        PlayerProfileManager.saveAll();
        logInfo("Plugin disabled");
    }

    // Instances

    public static DontPickUpPlugin getInstance() {
        return instance;
    }

    public static Language getLanguage() {
        return language;
    }

    // Reloading

    public static void reload() {
        language = new Language();
    }

    // Logging

    public static void logInfo(@NotNull String message) {
        getInstance().getLogger().info(message);
    }

    public static void logWarning(@NotNull String message) {
        getInstance().getLogger().warning(message);
    }

    public static void logSevere(@NotNull String message) {
        getInstance().getLogger().severe(message);
    }

    // Command Registration

    private boolean registerCommand(@NotNull String commandName, @NotNull CommandExecutor commandExecutor) {
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