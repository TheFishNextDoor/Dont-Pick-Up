package com.thefishnextdoor.dontpickup;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.thefishnextdoor.dontpickup.command.DPU;
import com.thefishnextdoor.dontpickup.event.Join;
import com.thefishnextdoor.dontpickup.event.PickUp;

public class DontPickUp extends JavaPlugin {

    private static DontPickUp instance;

    public void onEnable() {
        instance = this;

        FileSystem.setup(this);

        getCommand("dontpickup").setExecutor(new DPU());

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new Join(), this);
        pluginManager.registerEvents(new PickUp(), this);

        AutoSave.start();

        getLogger().info("Plugin enabled");
    }

    public void onDisable() {
        AutoSave.stop();
        TrackedPlayer.saveAll();
        getLogger().info("Plugin disabled");
    }

    public static DontPickUp getInstance() {
        return instance;
    }
}
