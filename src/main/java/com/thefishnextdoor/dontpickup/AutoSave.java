package com.thefishnextdoor.dontpickup;

public class AutoSave {

    final static int PERIOD = 1200;

    static int autoSaveTaskId = -1;

    public static void start() {
        if (autoSaveTaskId != -1) {
            return;
        }

        Plugin plugin = Plugin.getInstance();
        autoSaveTaskId = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                PlayerTracker.saveAll();
            }

        }, PERIOD, PERIOD).getTaskId();
    }

    public static void stop() {
        if (autoSaveTaskId != -1) {
            Plugin.getInstance().getServer().getScheduler().cancelTask(autoSaveTaskId);
            autoSaveTaskId = -1;
        }   
    }   
}
