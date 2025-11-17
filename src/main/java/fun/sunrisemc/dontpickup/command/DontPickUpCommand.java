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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.dontpickup.DontPickUpPlugin;
import fun.sunrisemc.dontpickup.config.Language;
import fun.sunrisemc.dontpickup.permission.Permissions;
import fun.sunrisemc.dontpickup.player.PlayerProfile;
import fun.sunrisemc.dontpickup.player.PlayerProfileManager;
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
                return getAllowedMaterialsAsStrings(player);
            }
            // /dontpickup remove [material]
            else if (subCommand.equalsIgnoreCase("remove")) {
                ArrayList<String> notPickingUp = getBlockedMaterialsAsStrings(player);
                notPickingUp.add("all");
                return notPickingUp;
            }
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        // Get language
        Language language = DontPickUpPlugin.getLanguage();

        // Get player
        if (!(sender instanceof Player)) {
            Language.sendMessage(sender, language.MUST_BE_PLAYER);
            return true;
        }

        Player player = (Player) sender;

        // Get the subcommand
        if (args.length < 1) {
            Language.sendMessage(player, language.HELP);
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
                Language.sendMessage(player, language.LIST_EMPTY);
                return true;
            }

            // List blocked materials
            Language.sendMessage(player, language.BLOCKED_MATERIALS_HEADER);
            for (Material material : notPickingUp) {
                String materialName = StringUtils.formatMaterial(material);
                ArrayList<String> message = Language.replaceVariable(language.BLOCKED_MATERIALS_MATERIAL, "<material>", materialName);
                Language.sendMessage(player, message);
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
                    Language.sendMessage(player, language.PICK_UP_ALL);
                    return true;
                }
                material = StringUtils.parseMaterial(materialInput);
            }
            else {
                material = getMaterialInHand(player);
            }

            if (material.isEmpty()) {
                Language.sendMessage(player, language.MISSING_MATERIAL);
                return true;
            }

            // Remove from blocked material list and notify player
            PlayerProfileManager.get(player).pickUp(material.get());
            String materialName = StringUtils.formatMaterial(material.get());
            Language.sendMessage(player, Language.replaceVariable(language.PICK_UP_MATERIAL, "<material>", materialName));
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
                material = getMaterialInHand(player);
            }

            if (material.isEmpty()) {
                Language.sendMessage(player, language.MISSING_MATERIAL);
                return true;
            }

            // Add to blocked material list and notify player
            PlayerProfileManager.get(player).dontPickUp(material.get());
            String materialName = StringUtils.formatMaterial(material.get());
            Language.sendMessage(player, Language.replaceVariable(language.DONT_PICKUP_MATERIAL, "<material>", materialName));
            return true;
        }
        // Reload
        else if (subCommand.equals("reload") && player.hasPermission(Permissions.RELOAD_PERMISSION)) {
            DontPickUpPlugin.reload();
            Language.sendMessage(player, language.PLUGIN_RELOADED);
            return true;
        }

        // Unknown subcommand
        Language.sendMessage(player, language.HELP);
        return true;
    }

    @NotNull
    private static ArrayList<String> getBlockedMaterialsAsStrings(@NotNull Player player) {
        ArrayList<String> blocked = new ArrayList<>();
        PlayerProfile trackedPlayer = PlayerProfileManager.get(player);
        for (Material material : Material.values()) {
            if (!trackedPlayer.canPickUp(material)) {
                blocked.add(material.name().toLowerCase());
            }
        }
        return blocked;
    }

    @NotNull
    private static ArrayList<String> getAllowedMaterialsAsStrings(@NotNull Player player) {
        ArrayList<String> allowed = new ArrayList<>();
        PlayerProfile trackedPlayer = PlayerProfileManager.get(player);
        for (Material material : Material.values()) {
            if (trackedPlayer.canPickUp(material)) {
                allowed.add(material.name().toLowerCase());
            }
        }
        return allowed;
    }

    private static Optional<Material> getMaterialInHand(@NotNull Player player) {
        PlayerInventory inventory = player.getInventory();

        ItemStack main = inventory.getItemInMainHand();
        if (main != null && main.getAmount() > 0 && !main.getType().isAir()) {
            return Optional.of(main.getType());
        }

        ItemStack off = inventory.getItemInOffHand();
        if (off != null && off.getAmount() > 0 && !off.getType().isAir()) {
            return Optional.of(off.getType());
        }

        return Optional.empty();
    }


}