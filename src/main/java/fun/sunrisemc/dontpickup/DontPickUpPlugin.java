package fun.sunrisemc.dontpickup;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.sunrisemc.dontpickup.command.DontPickUpCommand;
import fun.sunrisemc.dontpickup.config.LanguageConfig;
import fun.sunrisemc.dontpickup.event.PlayerJoin;
import fun.sunrisemc.dontpickup.file.ConfigFile;
import fun.sunrisemc.dontpickup.file.DataFile;
import fun.sunrisemc.dontpickup.file.PlayerDataFile;
import fun.sunrisemc.dontpickup.event.PickupItem;
import fun.sunrisemc.dontpickup.player.PlayerProfileManager;
import fun.sunrisemc.dontpickup.scheduler.AutoSave;
import fun.sunrisemc.dontpickup.utils.YAMLUtils;

public class DontPickUpPlugin extends JavaPlugin {

    // Plugin Instance

    private static @Nullable DontPickUpPlugin instance;

    // Configs

    private static @Nullable LanguageConfig languageConfig;

    // Java Plugin

    public void onEnable() {
        instance = this;

        applyUpdates();

        languageConfig = new LanguageConfig();

        registerCommand("dontpickup", new DontPickUpCommand());

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoin(), this);
        pluginManager.registerEvents(new PickupItem(), this);

        AutoSave.start();

        logInfo("Plugin enabled.");
    }

    public void onDisable() {
        AutoSave.stop();
        PlayerProfileManager.saveAll();
        logInfo("Plugin disabled.");
    }

    // Instances

    public static DontPickUpPlugin getInstance() {
        return instance;
    }

    public static LanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    // Reloading

    public static void reload() {
        languageConfig = new LanguageConfig();
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

    // Updates

    public static void applyUpdates() {
        // 1.2.1 -> 1.3.0: Player data files moved to new folder and format changed
        ArrayList<String> playerDataFileNames = new ArrayList<>();
        for (String name : DataFile.getNames()) {
            // Check that it is a player uuid (correct length)
            if (name.length() != 36) {
                continue;
            }

            playerDataFileNames.add(name);
        }

        if (!playerDataFileNames.isEmpty()) {
            DontPickUpPlugin.logInfo("Updating player data files to new 1.3.0 format...");

            for (String playerDataFileName : playerDataFileNames) {
                YamlConfiguration playerData = DataFile.get(playerDataFileName);

                UUID uuid = UUID.fromString(playerDataFileName);

                // Rename BlockedMaterials to blocked-materials
                if (playerData.contains("BlockedMaterials")) {
                    playerData.set("blocked-materials", playerData.get("BlockedMaterials"));
                    playerData.set("BlockedMaterials", null);
                }

                // Save as a player data file rather than a generic data file
                if (PlayerDataFile.save(uuid, playerData)) {
                    DataFile.delete(playerDataFileName);
                }
            }

            DontPickUpPlugin.logInfo("Player data files updated to 1.3.0 format.");
        }

        // 1.2.1 -> 1.3.0: Language file key updates
        YamlConfiguration oldLanguageConfig = ConfigFile.get("language", false);
        if (YAMLUtils.renameKey(oldLanguageConfig, "list-empty", "blocked-materials-list-empty")) {
            ConfigFile.save("language", oldLanguageConfig);
            DontPickUpPlugin.logInfo("Language file updated to 1.3.0 format.");
        }
    }
}