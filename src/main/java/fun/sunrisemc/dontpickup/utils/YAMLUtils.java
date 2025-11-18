package fun.sunrisemc.dontpickup.utils;

import org.bukkit.configuration.file.YamlConfiguration;

public class YAMLUtils {

    public static boolean renameKeyIfExists(YamlConfiguration config, String oldKey, String newKey) {
        if (config.contains(oldKey) && !config.contains(newKey)) {
            Object value = config.get(oldKey);
            config.set(newKey, value);
            config.set(oldKey, null);
            return true;
        }
        
        return false;
    }
}