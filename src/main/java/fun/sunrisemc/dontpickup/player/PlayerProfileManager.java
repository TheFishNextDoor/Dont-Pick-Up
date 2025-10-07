package fun.sunrisemc.dontpickup.player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.dontpickup.DontPickUpPlugin;

public class PlayerProfileManager {

    private static ConcurrentHashMap<UUID, PlayerProfile> playerProfiles = new ConcurrentHashMap<UUID, PlayerProfile>();

    public static PlayerProfile get(@NonNull Player player) {
        PlayerProfile profile = playerProfiles.get(player.getUniqueId());
        if (profile == null) {
            profile = new PlayerProfile(player);
            playerProfiles.put(player.getUniqueId(), profile);
        }
        return profile;
    }

    public static void preLoad(@NonNull final Player player) {
        DontPickUpPlugin plugin = DontPickUpPlugin.getInstance();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
    
            @Override
            public void run() {
                get(player);
            }
        
        });
    }

    public static void unload(@NonNull UUID id) {
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