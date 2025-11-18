package fun.sunrisemc.dontpickup.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.dontpickup.file.ConfigFile;

public class LanguageConfig {

    public final @NotNull ArrayList<String> HELP;
    public final @NotNull ArrayList<String> MUST_BE_PLAYER;
    public final @NotNull ArrayList<String> MISSING_MATERIAL;
    public final @NotNull ArrayList<String> INVALID_MATERIAL;
    public final @NotNull ArrayList<String> PICK_UP_ALL;
    public final @NotNull ArrayList<String> PICK_UP_MATERIAL;
    public final @NotNull ArrayList<String> DONT_PICKUP_MATERIAL;
    public final @NotNull ArrayList<String> BLOCKED_MATERIALS_LIST_EMPTY;
    public final @NotNull ArrayList<String> BLOCKED_MATERIALS_HEADER;
    public final @NotNull ArrayList<String> BLOCKED_MATERIALS_MATERIAL;
    public final @NotNull ArrayList<String> INVALID_COMMAND;
    public final @NotNull ArrayList<String> PLUGIN_RELOADED;

    public LanguageConfig() {
        YamlConfiguration languageConfig = ConfigFile.get("language", true);

        HELP = getValue(languageConfig, "help");
        MUST_BE_PLAYER = getValue(languageConfig, "must-be-player");
        MISSING_MATERIAL = getValue(languageConfig, "missing-material");
        INVALID_MATERIAL = getValue(languageConfig, "invalid-material");
        PICK_UP_ALL = getValue(languageConfig, "pick-up-all");
        PICK_UP_MATERIAL = getValue(languageConfig, "pick-up-material");
        DONT_PICKUP_MATERIAL = getValue(languageConfig, "dont-pickup-material");
        BLOCKED_MATERIALS_LIST_EMPTY = getValue(languageConfig, "blocked-materials-list-empty");
        BLOCKED_MATERIALS_HEADER = getValue(languageConfig, "blocked-materials-header");
        BLOCKED_MATERIALS_MATERIAL = getValue(languageConfig, "blocked-materials-material");
        INVALID_COMMAND = getValue(languageConfig, "invalid-command");
        PLUGIN_RELOADED = getValue(languageConfig, "plugin-reloaded");
    }

    @NotNull
    public static ArrayList<String> getValue(@NotNull YamlConfiguration config, @NotNull String key) {
        ArrayList<String> value = new ArrayList<>();
        List<String> lines = config.getStringList(key);
        for (String line : lines) {
            value.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        if (value.isEmpty()) {
            String line = config.getString(key);
            if (line != null) {
                value.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
        return value;
    }
    
    public static void sendMessage(@NotNull CommandSender sender, @NotNull ArrayList<String> message) {
        // Don't send empty messages
        if (message.size() == 1 && message.get(0).isEmpty()) {
            return;
        }
        
        // Send each line of the message
        for (String line : message) {
            sender.sendMessage(line);
        }
    }

    @NotNull
    public static ArrayList<String> replaceVariable(@NotNull ArrayList<String> messageIn, @NotNull String variable, @NotNull String replacement) {
        ArrayList<String> messageOut = new ArrayList<>();
        for (String line : messageIn) {
            String modifiedLine = line.replace(variable, replacement);
            messageOut.add(modifiedLine);
        }
        return messageOut;
    }
}