package fun.sunrisemc.dontpickup.utils;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class PlayerUtils {

    public static Optional<Material> getMaterialInHand(@NotNull Player player) {
        PlayerInventory inventory = player.getInventory();

        Material mainHandMaterial = inventory.getItemInMainHand().getType();
        if (!mainHandMaterial.isAir()) {
            return Optional.of(mainHandMaterial);
        }

        Material offHandMaterial = inventory.getItemInOffHand().getType();
        if (!offHandMaterial.isAir()) {
            return Optional.of(offHandMaterial);
        }

        return Optional.empty();
    }
}