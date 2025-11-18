package fun.sunrisemc.dontpickup.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.dontpickup.DontPickUpPlugin;
import fun.sunrisemc.dontpickup.file.PlayerDataFile;

public class PlayerProfile {

    private final @NotNull UUID uuid;

    private @NotNull HashSet<Material> blockedMaterials = new HashSet<>();

    private boolean changes = false;

    protected PlayerProfile(@NotNull Player player) {
        this.uuid = player.getUniqueId();
        load();
    }

    public @NotNull UUID getUUID() {
        return uuid;
    }

    public boolean isOnline() {
        return DontPickUpPlugin.getInstance().getServer().getPlayer(uuid) != null;
    }

    // Blocked Materials

    public boolean canPickupMaterial(@NotNull Material material) {
        return !blockedMaterials.contains(material);
    }

    public void blockMaterial(@NotNull Material material) {
        if (!blockedMaterials.contains(material)) {
            blockedMaterials.add(material);
            changes = true;
        }
    }

    public void unblockMaterial(@NotNull Material material) {
        if (blockedMaterials.remove(material)) {
            changes = true;
        }
    }

    public void unblockAllMaterials() {
        if (!blockedMaterials.isEmpty()) {
            blockedMaterials.clear();
            changes = true;
        }
    }

    @NotNull
    public Set<Material> getBlockedMaterials() {
        return Collections.unmodifiableSet(blockedMaterials);
    }

    // Loading and Saving

    private void load() {
        YamlConfiguration playerFile = PlayerDataFile.get(uuid);
        
        List<String> materialNames = playerFile.getStringList("blocked-materials");
        for (String materialName : materialNames) {
            Material material = Material.getMaterial(materialName);
            if (material != null) {
                blockedMaterials.add(material);
            }
        }
    }

    public void save() {
        if (changes) {
            YamlConfiguration playerFile = PlayerDataFile.get(uuid);
            
            ArrayList<String> materialNames = new ArrayList<>();
            for (Material material : blockedMaterials) {
                materialNames.add(material.name());
            }

            playerFile.set("blocked-materials", materialNames);

            if (PlayerDataFile.save(uuid, playerFile)) {
                changes = false;
            }
        }

        if (!changes && !isOnline()) {
            PlayerProfileManager.unload(uuid);
        }
    }
}