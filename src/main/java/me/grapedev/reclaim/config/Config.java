package me.grapedev.reclaim.config;

import me.grapedev.reclaim.Reclaim;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    // create instance
    private final static Config instance = new Config();

    private static File file;
    private YamlConfiguration config;

    public void load() {
        file = new File(Reclaim.getInstance().getDataFolder(), "config.yml");
        if (!file.exists()) {
            Reclaim.getInstance().saveResource("config.yml", false);
        }

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static YamlConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set(String path, Object value) {
        config.set(path, value);

        save();
    }

    public static Config getInstance() {
        return instance;
    }
}
