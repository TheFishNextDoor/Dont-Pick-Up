package fun.sunrisemc.dontpickup.scheduler;

import fun.sunrisemc.dontpickup.DontPickUpPlugin;
import fun.sunrisemc.dontpickup.player.PlayerProfileManager;

public class AutoSave {

    private final static int INTERVAL_TICKS = 1200;

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

        }, INTERVAL_TICKS, INTERVAL_TICKS).getTaskId();
    }

    public static void stop() {
        if (autoSaveTaskId != -1) {
            DontPickUpPlugin.getInstance().getServer().getScheduler().cancelTask(autoSaveTaskId);
            autoSaveTaskId = -1;
        }   
    }
}