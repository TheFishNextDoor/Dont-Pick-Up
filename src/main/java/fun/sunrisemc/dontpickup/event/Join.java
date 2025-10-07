package fun.sunrisemc.dontpickup.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fun.sunrisemc.dontpickup.player.PlayerProfileManager;

public class Join implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerProfileManager.preLoad(player);
    }
}