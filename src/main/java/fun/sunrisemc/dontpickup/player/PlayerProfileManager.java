package fun.sunrisemc.dontpickup.player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.dontpickup.DontPickUpPlugin;

public class PlayerProfileManager {

    private static @NotNull ConcurrentHashMap<UUID, PlayerProfile> playerProfiles = new ConcurrentHashMap<UUID, PlayerProfile>();

    @NotNull
    public static PlayerProfile get(@NotNull Player player) {
        UUID key = player.getUniqueId();
        PlayerProfile profile = playerProfiles.get(key);
        if (profile == null) {
            profile = new PlayerProfile(player);
            playerProfiles.put(key, profile);
        }
        return profile;
    }

    public static void preLoad(@NotNull final Player player) {
        DontPickUpPlugin plugin = DontPickUpPlugin.getInstance();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
    
            @Override
            public void run() {
                get(player);
            }
        
        });
    }

    public static void saveAll() {
        for (PlayerProfile profile : playerProfiles.values()) {
            profile.save();
        }
    }

    protected static void unload(@NotNull UUID uuid) {
        playerProfiles.remove(uuid);
    }
}