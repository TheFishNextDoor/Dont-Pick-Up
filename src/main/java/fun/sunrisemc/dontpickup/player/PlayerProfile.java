package fun.sunrisemc.dontpickup.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.dontpickup.DontPickUpPlugin;
import fun.sunrisemc.dontpickup.file.DataFile;

public class PlayerProfile {

    private final @NotNull UUID id;

    private @NotNull HashSet<Material> dontPickUp = new HashSet<>();

    private boolean changes = false;

    PlayerProfile(@NotNull Player player) {
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

    public boolean canPickUp(@NotNull Material material) {
        return !dontPickUp.contains(material);
    }

    public void dontPickUp(@NotNull Material material) {
        if (!dontPickUp.contains(material)) {
            dontPickUp.add(material);
            changes = true;
        }
    }

    public void pickUp(@NotNull Material material) {
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

    @NotNull
    private YamlConfiguration getPlayerFile() {
        return DataFile.get(id.toString());
    }
}