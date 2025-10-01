package com.thefishnextdoor.dontpickup.scheduler;

import com.thefishnextdoor.dontpickup.DontPickUpPlugin;
import com.thefishnextdoor.dontpickup.player.PlayerProfileManager;

public class AutoSave {

    private final static int PERIOD = 1200;

    private static int autoSaveTaskId = -1;

    public static void start() {
        if (autoSaveTaskId != -1) {
            return;
        }

        DontPickUpPlugin plugin = DontPickUpPlugin.getInstance();
        autoSaveTaskId = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                PlayerProfileManager.saveAll();
            }

        }, PERIOD, PERIOD).getTaskId();
    }

    public static void stop() {
        if (autoSaveTaskId != -1) {
            DontPickUpPlugin.getInstance().getServer().getScheduler().cancelTask(autoSaveTaskId);
            autoSaveTaskId = -1;
        }   
    }   
}
