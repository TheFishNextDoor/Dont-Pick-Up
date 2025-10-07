package fun.sunrisemc.dontpickup.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.dontpickup.DontPickUpPlugin;
import fun.sunrisemc.dontpickup.file.DataFile;

public class PlayerProfile {

    private final UUID id;

    private HashSet<Material> dontPickUp = new HashSet<>();

    private boolean changes = false;

    PlayerProfile(@NonNull Player player) {
        this.id = player.getUniqueId();

        YamlConfiguration playerFile = getPlayerFile();

        dontPickUp.clear();

        List<String> materialNames = playerFile.getStringList("BlockedMaterials");
        for (String name : materialNames) {
            Material material = Material.matchMaterial(name);
            if (material != null) {
                dontPickUp.add(material);
            }
        }
    }

    public boolean canPickUp(@NonNull Material material) {
        return !dontPickUp.contains(material);
    }

    public void dontPickUp(@NonNull Material material) {
        if (!dontPickUp.contains(material)) {
            dontPickUp.add(material);
            changes = true;
        }
    }

    public void pickUp(@NonNull Material material) {
        if (dontPickUp.remove(material)) {
            changes = true;
        }
    }

    public void pickUpAll() {
        if (!dontPickUp.isEmpty()) {
            dontPickUp.clear();
            changes = true;
        }
    }

    public void save() {
        if (changes) {
            YamlConfiguration playerFile = getPlayerFile();
            
            ArrayList<String> materialNames = new ArrayList<>();
            for (Material material : dontPickUp) {
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

    private boolean isOnline() {
        return DontPickUpPlugin.getInstance().getServer().getPlayer(id) != null;
    }

    private YamlConfiguration getPlayerFile() {
        return DataFile.get(id.toString());
    }
}