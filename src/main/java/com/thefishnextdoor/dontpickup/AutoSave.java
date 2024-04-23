package com.thefishnextdoor.dontpickup;

public class AutoSave {

    private final static int PERIOD = 1200;

    private static int autoSaveTaskId = -1;

    public static void start() {
        if (autoSaveTaskId != -1) {
            return;
        }

        DontPickUp plugin = DontPickUp.getInstance();
        autoSaveTaskId = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                TrackedPlayer.saveAll();
            }

        }, PERIOD, PERIOD).getTaskId();
    }

    public static void stop() {
        if (autoSaveTaskId != -1) {
            DontPickUp.getInstance().getServer().getScheduler().cancelTask(autoSaveTaskId);
            autoSaveTaskId = -1;
        }   
    }   
}
