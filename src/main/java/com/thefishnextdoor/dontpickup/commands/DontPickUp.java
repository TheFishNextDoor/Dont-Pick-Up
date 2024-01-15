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
                return getPickingUpAsString(player);
            }
            else if (subCommand.equals("remove")) {
                return getNotPickingUpAsStrings(player);
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
            sender.sendMessage(ChatColor.RED + "You must specify a subcommand.");
            return true;
        }

        String subCommand = args[0];

        if (subCommand.equals("list")) {
            ArrayList<String> notPickingUp = getNotPickingUpAsStrings(player);
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
            String materialName;
            if (args.length < 2) {
                ItemStack hand = player.getInventory().getItemInMainHand();
                ItemStack offHand = player.getInventory().getItemInOffHand();
                ItemStack item = hand.getType() == Material.AIR ? offHand : hand;
                if (item.getType() == Material.AIR) {
                    player.sendMessage(ChatColor.RED + "You must specify a material name.");
                    return true;
                }
                materialName = item.getType().name();
            }
            else {
                materialName = args[1];
            }

            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                player.sendMessage(ChatColor.RED + "Invalid material name.");
                return true;
            }

            PlayerTracker.get(player).pickUp(material);
            player.sendMessage(ChatColor.WHITE + "Now picking up " + titleCase(materialName) + ".");
            return true;
        }
        else if (subCommand.equals("add")) {
            String materialName;
            if (args.length < 2) {
                ItemStack hand = player.getInventory().getItemInMainHand();
                ItemStack offHand = player.getInventory().getItemInOffHand();
                ItemStack item = hand.getType() == Material.AIR ? offHand : hand;
                if (item.getType() == Material.AIR) {
                    player.sendMessage(ChatColor.RED + "You must specify a material name.");
                    return true;
                }
                materialName = item.getType().name();
            }
            else {
                materialName = args[1];
            }
            
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                player.sendMessage(ChatColor.RED + "Invalid material name.");
                return true;
            }

            PlayerTracker.get(player).dontPickUp(material);
            player.sendMessage(ChatColor.WHITE + "No longer picking up " + titleCase(materialName) + ".");
            return true;
        }

        player.sendMessage(ChatColor.RED + "Invalid subcommand.");
        return true;
    }

    private static ArrayList<String> getNotPickingUpAsStrings(Player player) {
        ArrayList<String> notPickingUp = new ArrayList<>();
        for (Material material : PlayerTracker.get(player).notPickingUp()) {
            notPickingUp.add(material.name().toLowerCase());
        }
        return notPickingUp;
    }

    private static ArrayList<String> getPickingUpAsString(Player player) {
        ArrayList<String> pickingUp = new ArrayList<>();
        ArrayList<Material> notPickingUp = PlayerTracker.get(player).notPickingUp();
        for (Material material : Material.values()) {
            if (!notPickingUp.contains(material)) {
                pickingUp.add(material.name().toLowerCase());
            }
        }
        return pickingUp;
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
