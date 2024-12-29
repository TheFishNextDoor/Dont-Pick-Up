package com.thefishnextdoor.dontpickup;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import com.thefishnextdoor.dontpickup.file.ConfigFile;

import net.md_5.bungee.api.ChatColor;

public class Language {

    public final ArrayList<String> HELP;
    public final ArrayList<String> MUST_BE_PLAYER;
    public final ArrayList<String> MISSING_MATERIAL;
    public final ArrayList<String> INVALID_MATERIAL;
    public final ArrayList<String> PICK_UP_ALL;
    public final ArrayList<String> PICK_UP_MATERIAL;
    public final ArrayList<String> DONT_PICKUP_MATERIAL;
    public final ArrayList<String> LIST_EMPTY;
    public final ArrayList<String> BLOCKED_MATERIALS_HEADER;
    public final ArrayList<String> BLOCKED_MATERIALS_MATERIAL;
    public final ArrayList<String> INVALID_COMMAND;

    public Language(DontPickUp plugin) {
        YamlConfiguration languageFile = ConfigFile.get("language");

        HELP = getValue(languageFile, "help");
        MUST_BE_PLAYER = getValue(languageFile, "must-be-player");
        MISSING_MATERIAL = getValue(languageFile, "missing-material");
        INVALID_MATERIAL = getValue(languageFile, "invalid-material");
        PICK_UP_ALL = getValue(languageFile, "pick-up-all");
        PICK_UP_MATERIAL = getValue(languageFile, "pick-up-material");
        DONT_PICKUP_MATERIAL = getValue(languageFile, "dont-pickup-material");
        LIST_EMPTY = getValue(languageFile, "list-empty");
        BLOCKED_MATERIALS_HEADER = getValue(languageFile, "blocked-materials-header");
        BLOCKED_MATERIALS_MATERIAL = getValue(languageFile, "blocked-materials-material");
        INVALID_COMMAND = getValue(languageFile, "invalid-command");
    }

    public static ArrayList<String> getValue(YamlConfiguration config, String key) {
        ArrayList<String> value = new ArrayList<>();
        for (String line : config.getStringList(key)) {
            value.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        if (value.isEmpty()) {
            value.add(ChatColor.translateAlternateColorCodes('&', config.getString(key)));
        }
        return value;
    }

    public static void sendMessage(CommandSender sender, ArrayList<String> message) {
        if (message.size() == 1 && message.get(0).isEmpty()) {
            return;
        }
        for (String line : message) {
            sender.sendMessage(line);
        }
    }

    public static ArrayList<String> replaceVariable(ArrayList<String> in, String variable, String replacement) {
        ArrayList<String> out = new ArrayList<>();
        for (String line : in) {
            out.add(line.replace(variable, replacement));
        }
        return out;
    }
}