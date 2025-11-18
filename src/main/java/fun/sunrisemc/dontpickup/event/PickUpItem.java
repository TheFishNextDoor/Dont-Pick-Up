package fun.sunrisemc.dontpickup.event;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.dontpickup.player.PlayerProfile;
import fun.sunrisemc.dontpickup.player.PlayerProfileManager;

public class PickupItem implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPickUp(@NotNull EntityPickupItemEvent event) {
        // Check if entity is player
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        // Get player profile
        PlayerProfile trackedPlayer = PlayerProfileManager.get((Player) entity);
        
        // Cancel the event if the player is not allowed to pick up the item
        Material material = event.getItem().getItemStack().getType();
        if (!trackedPlayer.canPickupMaterial(material)) {
            event.setCancelled(true);
        }
    }    
}