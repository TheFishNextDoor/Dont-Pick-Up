package com.thefishnextdoor.dontpickup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.thefishnextdoor.dontpickup.PlayerTracker;

public class Join implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerTracker.preLoad(event.getPlayer());
    }
}
