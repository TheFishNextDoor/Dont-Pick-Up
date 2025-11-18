package fun.sunrisemc.dontpickup.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.dontpickup.DontPickUpPlugin;
import fun.sunrisemc.dontpickup.config.LanguageConfig;
import fun.sunrisemc.dontpickup.permission.Permissions;
import fun.sunrisemc.dontpickup.player.PlayerProfile;
import fun.sunrisemc.dontpickup.player.PlayerProfileManager;
import fun.sunrisemc.dontpickup.utils.PlayerUtils;
import fun.sunrisemc.dontpickup.utils.StringUtils;

public class DontPickUpCommand implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        // Get player
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;

        // /dontpickup <subcommand>
        if (args.length == 1) {
            if (sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
                return List.of("add", "remove", "list", "reload");
            }
            else{
                return List.of("add", "remove", "list");
            }
        }
        else if (args.length >= 2) {
            String subCommand = args[0];

            // /dontpickup add [material]
            if (subCommand.equalsIgnoreCase("add")) {
                PlayerProfile playerProfile = PlayerProfileManager.get(player);

                ArrayList<String> pickingUp = new ArrayList<>();
                for (Material material : Material.values()) {
                    if (playerProfile.canPickUp(material)) {
                        String materialName = StringUtils.formatMaterial(material);
                        pickingUp.add(materialName);
                    }
                }
                return pickingUp;
            }
            // /dontpickup remove [material]
            else if (subCommand.equalsIgnoreCase("remove")) {
                PlayerProfile playerProfile = PlayerProfileManager.get(player);

                ArrayList<String> notPickingUp = new ArrayList<>();
                for (Material material : playerProfile.getBlockedMaterials()) {
                    String materialName = StringUtils.formatMaterial(material);
                    notPickingUp.add(materialName);
                }
                notPickingUp.add("all");

                return notPickingUp;
            }
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        // Get language
        LanguageConfig languageConfig = DontPickUpPlugin.getLanguageConfig();

        // Get player
        if (!(sender instanceof Player)) {
            LanguageConfig.sendMessage(sender, languageConfig.MUST_BE_PLAYER);
            return true;
        }

        Player player = (Player) sender;

        // Get the subcommand
        if (args.length < 1) {
            LanguageConfig.sendMessage(player, languageConfig.HELP);
            return true;
        }

        String subCommand = args[0];

        // List
        if (subCommand.equals("list")) {
            // Get player profile
            PlayerProfile playerProfile = PlayerProfileManager.get(player);

            // Get blocked materials
            Set<Material> notPickingUp = playerProfile.getBlockedMaterials();
            if (notPickingUp.size() == 0) {
                LanguageConfig.sendMessage(player, languageConfig.BLOCKED_MATERIALS_LIST_EMPTY);
                return true;
            }

            // List blocked materials
            LanguageConfig.sendMessage(player, languageConfig.BLOCKED_MATERIALS_HEADER);
            for (Material material : notPickingUp) {
                String materialName = StringUtils.formatMaterial(material);
                ArrayList<String> message = LanguageConfig.replaceVariable(languageConfig.BLOCKED_MATERIALS_MATERIAL, "<material>", materialName);
                LanguageConfig.sendMessage(player, message);
            }
            return true;
        }
        // Remove
        else if (subCommand.equals("remove")) {
            // Determine material
            Optional<Material> material;
            if (args.length >= 2) {
                String materialInput = args[1];
                if (materialInput.equalsIgnoreCase("all")) {
                    PlayerProfileManager.get(player).pickUpAll();
                    LanguageConfig.sendMessage(player, languageConfig.PICK_UP_ALL);
                    return true;
                }
                material = StringUtils.parseMaterial(materialInput);
            }
            else {
                material = PlayerUtils.getMaterialInHand(player);
            }

            // Check material
            if (material.isEmpty()) {
                LanguageConfig.sendMessage(player, languageConfig.MISSING_MATERIAL);
                return true;
            }

            // Remove from blocked material list and notify player
            PlayerProfileManager.get(player).pickUp(material.get());
            String materialName = StringUtils.formatMaterial(material.get());
            LanguageConfig.sendMessage(player, LanguageConfig.replaceVariable(languageConfig.PICK_UP_MATERIAL, "<material>", materialName));
            return true;
        }
        // Add
        else if (subCommand.equals("add")) {
            // Determine material
            Optional<Material> material;
            if (args.length >= 2) {
                material = StringUtils.parseMaterial(args[1]);
            }
            else {
                material = PlayerUtils.getMaterialInHand(player);
            }

            // Check material
            if (material.isEmpty()) {
                LanguageConfig.sendMessage(player, languageConfig.MISSING_MATERIAL);
                return true;
            }

            // Add to blocked material list and notify player
            PlayerProfileManager.get(player).dontPickUp(material.get());
            String materialName = StringUtils.formatMaterial(material.get());
            LanguageConfig.sendMessage(player, LanguageConfig.replaceVariable(languageConfig.DONT_PICKUP_MATERIAL, "<material>", materialName));
            return true;
        }
        // Reload
        else if (subCommand.equals("reload") && player.hasPermission(Permissions.RELOAD_PERMISSION)) {
            DontPickUpPlugin.reload();
            LanguageConfig.sendMessage(player, languageConfig.PLUGIN_RELOADED);
            return true;
        }

        // Unknown subcommand
        LanguageConfig.sendMessage(player, languageConfig.HELP);
        return true;
    }
}