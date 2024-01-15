package com.thefishnextdoor.dontpickup.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import com.thefishnextdoor.dontpickup.PlayerTracker;
import com.thefishnextdoor.dontpickup.PlayerTracker.TrackedPlayer;

public class PickUp implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPickUp(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        TrackedPlayer trackedPlayer = PlayerTracker.get((Player) entity);
        if (trackedPlayer.notPickingUp().contains(event.getItem().getItemStack().getType())) {
            event.setCancelled(true);
        }
    }    
}
