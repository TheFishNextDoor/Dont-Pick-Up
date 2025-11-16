package fun.sunrisemc.dontpickup;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.dontpickup.command.DontPickUpCommand;
import fun.sunrisemc.dontpickup.config.Language;
import fun.sunrisemc.dontpickup.event.PlayerJoin;
import fun.sunrisemc.dontpickup.event.PickUpItem;
import fun.sunrisemc.dontpickup.player.PlayerProfileManager;
import fun.sunrisemc.dontpickup.scheduler.AutoSave;

public class DontPickUpPlugin extends JavaPlugin {

    private static DontPickUpPlugin instance;

    private static Language language;

    public void onEnable() {
        instance = this;

        loadConfigs();

        registerCommand("dontpickup", new DontPickUpCommand());

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoin(), this);
        pluginManager.registerEvents(new PickUpItem(), this);

        AutoSave.start();

        logInfo("Plugin enabled");
    }

    public void onDisable() {
        AutoSave.stop();
        PlayerProfileManager.saveAll();
        logInfo("Plugin disabled");
    }

    public static void loadConfigs() {
        language = new Language();
    }

    public static DontPickUpPlugin getInstance() {
        return instance;
    }

    public static Language getLanguage() {
        return language;
    }

    public static void logInfo(@NonNull String message) {
        getInstance().getLogger().info(message);
    }

    public static void logWarning(@NonNull String message) {
        getInstance().getLogger().warning(message);
    }

    public static void logSevere(@NonNull String message) {
        getInstance().getLogger().severe(message);
    }

    private boolean registerCommand(@NonNull String commandName, @NonNull CommandExecutor commandExecutor) {
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