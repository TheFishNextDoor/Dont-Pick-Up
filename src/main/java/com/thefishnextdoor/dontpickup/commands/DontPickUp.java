package com.thefishnextdoor.dontpickup.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.thefishnextdoor.dontpickup.PlayerTracker;
import com.thefishnextdoor.dontpickup.PlayerTracker.TrackedPlayer;

import net.md_5.bungee.api.ChatColor;

public class DontPickUp implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            ArrayList<String> subCommands = new ArrayList<>();
            subCommands.add("add");
            subCommands.add("remove");
            subCommands.add("list");
            return subCommands;
        }
        else if (args.length >= 2) {
            String subCommand = args[0];
            if (subCommand.equals("add")) {
                return getAllowedMaterialsAsStrings(player);
            }
            else if (subCommand.equals("remove")) {
                ArrayList<String> notPickingUp = getBlockedMaterialsAsStrings(player);
                notPickingUp.add("all");
                return notPickingUp;
            }
        }

        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "DontPickUp");
            player.sendMessage(ChatColor.RED + "/dontpickup add " + ChatColor.WHITE + "Disable picking up the item in your hand");
            player.sendMessage(ChatColor.RED + "/dontpickup add [material] " + ChatColor.WHITE + "Disable picking up the specified material");
            player.sendMessage(ChatColor.RED + "/dontpickup remove " + ChatColor.WHITE + "Enable picking up the item in your hand");
            player.sendMessage(ChatColor.RED + "/dontpickup remove [material] " + ChatColor.WHITE + "Enable picking up the specified material");
            player.sendMessage(ChatColor.RED + "/dontpickup remove all " + ChatColor.WHITE + "Enable picking up all items");
            player.sendMessage(ChatColor.RED + "/dontpickup list " + ChatColor.WHITE + "List all items you are not picking up");
            return true;
        }

        String subCommand = args[0];

        if (subCommand.equals("list")) {
            ArrayList<String> notPickingUp = getBlockedMaterialsAsStrings(player);
            if (notPickingUp.size() == 0) {
                player.sendMessage(ChatColor.YELLOW + "You are picking up all items.");
                return true;
            }

            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Blocked Items");
            for (String material : notPickingUp) {
                player.sendMessage(ChatColor.RESET + "- " + titleCase(material));
            }
            return true;
        }
        else if (subCommand.equals("remove")) {
            String materialName = materialName(player, args);
            if (materialName == null) {
                player.sendMessage(ChatColor.RED + "You must specify a material name.");
                return true;
            }

            if (materialName.equalsIgnoreCase("all")) {
                PlayerTracker.get(player).pickUpAll();
                player.sendMessage(ChatColor.WHITE + "Now picking up all items.");
                return true;
            }

            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                player.sendMessage(ChatColor.RED + "Invalid material name.");
                return true;
            }

            PlayerTracker.get(player).pickUp(material);
            player.sendMessage(ChatColor.WHITE + "Now picking up " + materialName.toLowerCase().replaceAll("_", " ") + ".");
            return true;
        }
        else if (subCommand.equals("add")) {
            String materialName = materialName(player, args);
            if (materialName == null) {
                player.sendMessage(ChatColor.RED + "You must specify a material name.");
                return true;
            }
            
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                player.sendMessage(ChatColor.RED + "Invalid material name.");
                return true;
            }

            PlayerTracker.get(player).dontPickUp(material);
            player.sendMessage(ChatColor.WHITE + "No longer picking up " + materialName.toLowerCase().replaceAll("_", " ") + ".");
            return true;
        }

        player.sendMessage(ChatColor.RED + "Invalid subcommand.");
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
        TrackedPlayer trackedPlayer = PlayerTracker.get(player);
        for (Material material : Material.values()) {
            if (!trackedPlayer.canPickUp(material)) {
                blocked.add(material.name().toLowerCase());
            }
        }
        return blocked;
    }

    private static ArrayList<String> getAllowedMaterialsAsStrings(Player player) {
        ArrayList<String> allowed = new ArrayList<>();
        TrackedPlayer trackedPlayer = PlayerTracker.get(player);
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
