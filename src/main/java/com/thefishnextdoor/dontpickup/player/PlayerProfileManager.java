package com.thefishnextdoor.dontpickup.player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.thefishnextdoor.dontpickup.DontPickUpPlugin;

public class PlayerProfileManager {

    private static ConcurrentHashMap<UUID, PlayerProfile> playerProfiles = new ConcurrentHashMap<UUID, PlayerProfile>();

    public static PlayerProfile get(Player player) {
        PlayerProfile profile = playerProfiles.get(player.getUniqueId());
        if (profile == null) {
            profile = new PlayerProfile(player);
            playerProfiles.put(player.getUniqueId(), profile);
        }
        return profile;
    }

    public static void preLoad(final Player player) {
        DontPickUpPlugin plugin = DontPickUpPlugin.getInstance();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
    
            @Override
            public void run() {
                get(player);
            }
        
        });
    }

    public static void unload(UUID id) {
        PlayerProfile profile = playerProfiles.remove(id);
        if (profile != null) {
            profile.save();
        }
    }

    public static void saveAll() {
        for (PlayerProfile profile : playerProfiles.values()) {
            profile.save();
        }
    }
}