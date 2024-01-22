package com.thefishnextdoor.dontpickup;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.thefishnextdoor.dontpickup.commands.DontPickUp;
import com.thefishnextdoor.dontpickup.events.Join;
import com.thefishnextdoor.dontpickup.events.PickUp;

public class Plugin extends JavaPlugin {

    public static final Logger LOGGER = Logger.getLogger("Don't Pickup");

    private static Plugin instance;

    public void onEnable() {
        instance = this;

        FileSystem.setup(this);

        getCommand("dontpickup").setExecutor(new DontPickUp());

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new Join(), this);
        pluginManager.registerEvents(new PickUp(), this);

        AutoSave.start();

        LOGGER.info("Plugin enabled");
    }

    public void onDisable() {
        AutoSave.stop();
        PlayerTracker.saveAll();
        LOGGER.info("Plugin disabled");
    }

    public static Plugin getInstance() {
        return instance;
    }
}
