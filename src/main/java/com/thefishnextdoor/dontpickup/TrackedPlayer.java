package com.thefishnextdoor.dontpickup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.thefishnextdoor.dontpickup.file.DataFile;

public class TrackedPlayer {

    private static ConcurrentHashMap<UUID, TrackedPlayer> trackedPlayers = new ConcurrentHashMap<UUID, TrackedPlayer>();

    private final UUID id;

    private HashSet<Material> dontPickUp = new HashSet<>();
    private boolean changes = false;

    private TrackedPlayer(Player player) {
        this.id = player.getUniqueId();
        load();
        trackedPlayers.put(this.id, this);
    }

    public boolean canPickUp(Material material) {
        return !dontPickUp.contains(material);
    }

    public void dontPickUp(Material material) {
        if (!dontPickUp.contains(material)) {
            dontPickUp.add(material);
            changes = true;
        }
    }

    public void pickUp(Material material) {
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

    private void load() {
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

    private void save() {
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
            trackedPlayers.remove(id);
        }
    }

    private boolean isOnline() {
        return DontPickUp.getInstance().getServer().getPlayer(id) != null;
    }

    private YamlConfiguration getPlayerFile() {
        return DataFile.get(id.toString());
    }

    public static TrackedPlayer get(Player player) {
        TrackedPlayer trackedPlayer = trackedPlayers.get(player.getUniqueId());
        return trackedPlayer != null ? trackedPlayer : new TrackedPlayer(player);
    }

    public static void preLoad(final Player player) {
        DontPickUp plugin = DontPickUp.getInstance();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
    
            @Override
            public void run() {
                get(player);
            }
    
        });
    }

    public static void saveAll() {
        for (TrackedPlayer trackedPlayer : trackedPlayers.values()) {
            trackedPlayer.save();
        }
    }
}