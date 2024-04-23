package com.thefishnextdoor.dontpickup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

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
        File playerFile = getPlayerFile();
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
            File playerFile = getPlayerFile();
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
                DontPickUp.getInstance().getLogger().severe("Failed to save player data for " + id + ".");
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
        return DontPickUp.getInstance().getServer().getPlayer(id) != null;
    }

    private File getPlayerFile() {
        return new File(getPlayerDataFolder(), id.toString() + ".yml");
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

    private static File getPlayerDataFolder() {
        File pluginFolder = DontPickUp.getInstance().getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        File dataFolder = new File(pluginFolder, "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        return dataFolder;
    }
}