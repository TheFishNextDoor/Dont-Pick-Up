package fun.sunrisemc.dontpickup.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.dontpickup.player.PlayerProfileManager;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerProfileManager.preLoad(player);
    }
}