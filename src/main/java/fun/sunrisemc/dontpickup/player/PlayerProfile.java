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
import fun.sunrisemc.dontpickup.file.DataFile;

public class PlayerProfile {

    private final @NotNull UUID id;

    private @NotNull HashSet<Material> blockedMaterials = new HashSet<>();

    private boolean changes = false;

    PlayerProfile(@NotNull Player player) {
        this.id = player.getUniqueId();
        load();
    }

    // Blocked Materials

    public boolean canPickUp(@NotNull Material material) {
        return !blockedMaterials.contains(material);
    }

    public void dontPickUp(@NotNull Material material) {
        if (!blockedMaterials.contains(material)) {
            blockedMaterials.add(material);
            changes = true;
        }
    }

    public void pickUp(@NotNull Material material) {
        if (blockedMaterials.remove(material)) {
            changes = true;
        }
    }

    public void pickUpAll() {
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
        YamlConfiguration playerFile = getPlayerFile();
        
        List<String> materialNames = playerFile.getStringList("BlockedMaterials");
        for (String materialName : materialNames) {
            Material material = Material.getMaterial(materialName);
            if (material != null) {
                blockedMaterials.add(material);
            }
        }
    }

    public void save() {
        if (changes) {
            YamlConfiguration playerFile = getPlayerFile();
            
            ArrayList<String> materialNames = new ArrayList<>();
            for (Material material : blockedMaterials) {
                materialNames.add(material.name());
            }

            playerFile.set("BlockedMaterials", materialNames);

            if (DataFile.save(id.toString(), playerFile)) {
                changes = false;
            }
        }

        if (!changes && !isOnline()) {
            PlayerProfileManager.unload(id);
        }
    }

    @NotNull
    private YamlConfiguration getPlayerFile() {
        String dataFileName = id.toString();
        return DataFile.get(dataFileName);
    }

    // Utils

    private boolean isOnline() {
        return DontPickUpPlugin.getInstance().getServer().getPlayer(id) != null;
    }
}