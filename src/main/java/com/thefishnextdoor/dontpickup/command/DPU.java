package com.thefishnextdoor.dontpickup.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.thefishnextdoor.dontpickup.DontPickUp;
import com.thefishnextdoor.dontpickup.Language;
import com.thefishnextdoor.dontpickup.TrackedPlayer;

public class DPU implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            return List.of("add", "remove", "list");
        }
        else if (args.length >= 2) {
            String subCommand = args[0];
            if (subCommand.equalsIgnoreCase("add")) {
                return getAllowedMaterialsAsStrings(player);
            }
            else if (subCommand.equalsIgnoreCase("remove")) {
                ArrayList<String> notPickingUp = getBlockedMaterialsAsStrings(player);
                notPickingUp.add("all");
                return notPickingUp;
            }
        }

        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Language language = DontPickUp.getLanguage();

        if (!(sender instanceof Player)) {
            Language.sendMessage(sender, language.MUST_BE_PLAYER);
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
            Language.sendMessage(player, language.HELP);
            return true;
        }

        String subCommand = args[0];

        if (subCommand.equals("list")) {
            ArrayList<String> notPickingUp = getBlockedMaterialsAsStrings(player);
            if (notPickingUp.size() == 0) {
                Language.sendMessage(player, language.LIST_EMPTY);
                return true;
            }

            Language.sendMessage(player, language.BLOCKED_MATERIALS_HEADER);
            for (String material : notPickingUp) {
                String materialNameFormatted = titleCase(material);
                Language.sendMessage(player, Language.replaceVariable(language.BLOCKED_MATERIALS_MATERIAL, "<material>", materialNameFormatted));
            }
            return true;
        }
        else if (subCommand.equals("remove")) {
            String materialName = materialName(player, args);
            if (materialName == null) {
                Language.sendMessage(player, language.MISSING_MATERIAL);
                return true;
            }

            if (materialName.equalsIgnoreCase("all")) {
                TrackedPlayer.get(player).pickUpAll();
                Language.sendMessage(player, language.PICK_UP_ALL);
                return true;
            }

            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                Language.sendMessage(player, language.INVALID_MATERIAL);
                return true;
            }

            TrackedPlayer.get(player).pickUp(material);
            String materialNameFormatted = materialName.toLowerCase().replaceAll("_", " ");
            Language.sendMessage(player, Language.replaceVariable(language.PICK_UP_MATERIAL, "<material>", materialNameFormatted));
            return true;
        }
        else if (subCommand.equals("add")) {
            String materialName = materialName(player, args);
            if (materialName == null) {
                Language.sendMessage(player, language.MISSING_MATERIAL);
                return true;
            }
            
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                Language.sendMessage(player, language.INVALID_MATERIAL);
                return true;
            }

            TrackedPlayer.get(player).dontPickUp(material);
            String materialNameFormatted = materialName.toLowerCase().replaceAll("_", " ");
            Language.sendMessage(player, Language.replaceVariable(language.DONT_PICKUP_MATERIAL, "<material>", materialNameFormatted));
            return true;
        }

        Language.sendMessage(player, language.INVALID_COMMAND);
        return true;
    }

    private static String materialName(Player player, String[] args) {
        if (args.length < 2) {
            ItemStack hand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();
            ItemStack item = hand.getType() == Material.AIR ? offHand : hand;
            if (item.getType() == Material.AIR) {
                return null;
            }
            return item.getType().name();
        }
        else {
            return args[1];
        }
    }

    private static ArrayList<String> getBlockedMaterialsAsStrings(Player player) {
        ArrayList<String> blocked = new ArrayList<>();
        TrackedPlayer trackedPlayer = TrackedPlayer.get(player);
        for (Material material : Material.values()) {
            if (!trackedPlayer.canPickUp(material)) {
                blocked.add(material.name().toLowerCase());
            }
        }
        return blocked;
    }

    private static ArrayList<String> getAllowedMaterialsAsStrings(Player player) {
        ArrayList<String> allowed = new ArrayList<>();
        TrackedPlayer trackedPlayer = TrackedPlayer.get(player);
        for (Material material : Material.values()) {
            if (trackedPlayer.canPickUp(material)) {
                allowed.add(material.name().toLowerCase());
            }
        }
        return allowed;
    }

    private static String titleCase(String str) {
        str = str.replace("_", " ");
        String[] words = str.split(" ");
        String titleCase = "";
        for (String word : words) {
            titleCase += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
        }
        return titleCase.trim();
    }
}
