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
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        PlayerProfile trackedPlayer = PlayerProfileManager.get((Player) entity);
        
        Material material = event.getItem().getItemStack().getType();
        if (!trackedPlayer.canPickUp(material)) {
            event.setCancelled(true);
        }
    }    
}