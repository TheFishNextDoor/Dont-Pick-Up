package com.thefishnextdoor.dontpickup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerTracker {

    private static HashMap<UUID, TrackedPlayer> trackedPlayers = new HashMap<UUID, TrackedPlayer>();
    

    public static class TrackedPlayer {

        private final UUID id;
        private ArrayList<Material> dontPickUp = new ArrayList<>();
        private boolean changes = false;

        public TrackedPlayer(Player player) {
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
            File playerFile = FileSystem.getPlayerFile(id);
            if (!playerFile.exists()) {
                return;
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            dontPickUp.clear();

            List<String> materialNames = config.getStringList("BlockedMaterials");
            for (String name : materialNames) {
                Material material = Material.matchMaterial(name);
                if (material != null) {
                    dontPickUp.add(material);
                }
            }
        }

        private void save() {
            if (changes) {
                File playerFile = FileSystem.getPlayerFile(id);
                FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
                
                ArrayList<String> materialNames = new ArrayList<>();
                for (Material material : dontPickUp) {
                    materialNames.add(material.name());
                }
    
                config.set("BlockedMaterials", materialNames);
    
                try {
                    config.save(playerFile);
                } 
                catch (IOException e) {
                    Plugin.getInstance().LOGGER.severe("Failed to save player data for " + id + ".");
                    e.printStackTrace();
                }
                finally {
                    changes = false;
                }
            }

            if (!changes && !isOnline()) {
                trackedPlayers.remove(id);
            }
        }

        private boolean isOnline() {
            return Plugin.getInstance().getServer().getPlayer(id) != null;
        }
    }

    public static TrackedPlayer get(Player player) {
        TrackedPlayer trackedPlayer = trackedPlayers.get(player.getUniqueId());
        return trackedPlayer != null ? trackedPlayer : new TrackedPlayer(player);
    }

    public static void preLoad(final Player player) {
        Plugin plugin = Plugin.getInstance();
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
