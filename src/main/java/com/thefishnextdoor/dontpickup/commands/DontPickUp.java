package com.thefishnextdoor.dontpickup.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.thefishnextdoor.dontpickup.PlayerTracker;

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
            sender.sendMessage("You must be a player to use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage("You must specify a subcommand.");
            return true;
        }

        String subCommand = args[0];

        if (subCommand.equals("list")) {
            ArrayList<String> notPickingUp = getNotPickingUpAsStrings(player);
            if (notPickingUp.size() == 0) {
                player.sendMessage("You are picking up all items.");
                return true;
            }

            player.sendMessage("You are not picking up:");
            for (String material : notPickingUp) {
                player.sendMessage(material);
            }
            return true;
        }
        else if (subCommand.equals("remove")) {
            if (args.length < 2) {
                player.sendMessage("You must specify a material name.");
                return true;
            }

            String materialName = args[1];
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                player.sendMessage("Invalid material name.");
                return true;
            }

            PlayerTracker.get(player).pickUp(material);
            player.sendMessage("You are now picking up " + materialName + ".");
            return true;
        }
        else if (subCommand.equals("add")) {
            if (args.length < 2) {
                player.sendMessage("You must specify a material name.");
                return true;
            }

            String materialName = args[1];
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                player.sendMessage("Invalid material name.");
                return true;
            }

            PlayerTracker.get(player).dontPickUp(material);
            player.sendMessage("You are no longer picking up " + materialName + ".");
            return true;
        }

        player.sendMessage("Invalid subcommand.");
        return true;
    }

    private static ArrayList<String> getNotPickingUpAsStrings(Player player) {
        ArrayList<String> notPickingUp = new ArrayList<>();
        for (Material material : PlayerTracker.get(player).notPickingUp()) {
            notPickingUp.add(material.name());
        }
        return notPickingUp;
    }

    private static ArrayList<String> getPickingUpAsString(Player player) {
        ArrayList<String> pickingUp = new ArrayList<>();
        ArrayList<Material> notPickingUp = PlayerTracker.get(player).notPickingUp();
        for (Material material : Material.values()) {
            if (!notPickingUp.contains(material)) {
                pickingUp.add(material.name());
            }
        }
        return pickingUp;
    }
}
