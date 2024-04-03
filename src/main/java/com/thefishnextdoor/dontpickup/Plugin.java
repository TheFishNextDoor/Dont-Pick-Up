package com.thefishnextdoor.dontpickup;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.thefishnextdoor.dontpickup.command.DontPickUp;
import com.thefishnextdoor.dontpickup.event.Join;
import com.thefishnextdoor.dontpickup.event.PickUp;

public class Plugin extends JavaPlugin {

    public final Logger LOGGER = Logger.getLogger(getName());

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
