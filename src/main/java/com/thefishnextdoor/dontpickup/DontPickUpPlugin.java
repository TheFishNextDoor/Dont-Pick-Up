package com.thefishnextdoor.dontpickup;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.thefishnextdoor.dontpickup.command.DPU;
import com.thefishnextdoor.dontpickup.config.Language;
import com.thefishnextdoor.dontpickup.event.Join;
import com.thefishnextdoor.dontpickup.event.PickUp;
import com.thefishnextdoor.dontpickup.player.PlayerProfileManager;
import com.thefishnextdoor.dontpickup.scheduler.AutoSave;

public class DontPickUpPlugin extends JavaPlugin {

    private static DontPickUpPlugin instance;

    private static Language language;

    public void onEnable() {
        instance = this;

        loadConfigs();

        DPU dpu = new DPU();
        PluginCommand dpuCommand = getCommand("dontpickup");
        dpuCommand.setExecutor(dpu);
        dpuCommand.setTabCompleter(dpu);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new Join(), this);
        pluginManager.registerEvents(new PickUp(), this);

        AutoSave.start();

        getLogger().info("Plugin enabled");
    }

    public void onDisable() {
        AutoSave.stop();
        PlayerProfileManager.saveAll();
        getLogger().info("Plugin disabled");
    }

    public static void loadConfigs() {
        language = new Language(instance);
    }

    public static DontPickUpPlugin getInstance() {
        return instance;
    }

    public static Language getLanguage() {
        return language;
    }
}
