package com.thefishnextdoor.dontpickup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.thefishnextdoor.dontpickup.PlayerTracker;

public class Quit implements Listener {
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerTracker.remove(event.getPlayer());
    }
}
