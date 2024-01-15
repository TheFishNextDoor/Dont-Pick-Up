package com.thefishnextdoor.dontpickup;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.thefishnextdoor.dontpickup.commands.DontPickUp;
import com.thefishnextdoor.dontpickup.events.Join;
import com.thefishnextdoor.dontpickup.events.PickUp;
import com.thefishnextdoor.dontpickup.events.Quit;

public class Plugin extends JavaPlugin {

    public static final Logger LOGGER = Logger.getLogger("Don't Pickup");

    private static Plugin instance;

    public void onEnable() {
        instance = this;

        FileSystem.setup(this);

        getCommand("dontpickup").setExecutor(new DontPickUp());

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new Join(), this);
        pluginManager.registerEvents(new Quit(), this);
        pluginManager.registerEvents(new PickUp(), this);

        PlayerTracker.startASyncAutoSave();

        PlayerTracker.loadOnlinePlayers();

        LOGGER.info("Plugin enabled");
    }

    public void onDisable() {
        PlayerTracker.saveAll();
        LOGGER.info("Plugin disabled");
    }

    public static Plugin getInstance() {
        return instance;
    }
}
