package fun.sunrisemc.dontpickup.utils;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class PlayerUtils {

    public static Optional<Material> getMaterialInHand(@NotNull Player player) {
        PlayerInventory inventory = player.getInventory();

        // Check main hand
        Material mainHandMaterial = inventory.getItemInMainHand().getType();
        if (!mainHandMaterial.isAir()) {
            return Optional.of(mainHandMaterial);
        }

        // Check off hand
        Material offHandMaterial = inventory.getItemInOffHand().getType();
        if (!offHandMaterial.isAir()) {
            return Optional.of(offHandMaterial);
        }

        // No material in either hand
        return Optional.empty();
    }
}