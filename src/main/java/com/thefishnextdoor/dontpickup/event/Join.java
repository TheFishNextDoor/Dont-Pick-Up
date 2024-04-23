package com.thefishnextdoor.dontpickup.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.thefishnextdoor.dontpickup.TrackedPlayer;

public class Join implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        TrackedPlayer.preLoad(event.getPlayer());
    }
}
