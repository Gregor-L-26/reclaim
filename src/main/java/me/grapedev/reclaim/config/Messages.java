package me.grapedev.reclaim.config;

import me.grapedev.reclaim.Reclaim;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Messages {

    // create instance
    private final static Messages instance = new Messages();

    private File file;
    private YamlConfiguration config;

    // configurable variables
    public static String PLAYERS_ONLY, ALREADY_CLAIMED, RECLAIM_SUCCESS, NO_PERMISSION, PLAYER_NOT_FOUND, RESET_RECLAIM, INVALID_RESET_USAGE, RESET_RECLAIM_ALL;

    public void load() {
        file = new File(Reclaim.getInstance().getDataFolder(), "messages.yml");
        if (!file.exists()) {
            Reclaim.getInstance().saveResource("messages.yml", false);
        }

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PLAYERS_ONLY = config.getString("PLAYERS-ONLY");
        ALREADY_CLAIMED = config.getString("ALREADY-CLAIMED");
        RECLAIM_SUCCESS = config.getString("RECLAIM-SUCCESS");
        NO_PERMISSION = config.getString("NO-PERMISSION");
        PLAYER_NOT_FOUND = config.getString("PLAYER-NOT-FOUND");
        RESET_RECLAIM = config.getString("RESET-RECLAIM");
        INVALID_RESET_USAGE = config.getString("INVALID-RESET-USAGE");
        RESET_RECLAIM_ALL = config.getString("RESET-RECLAIM-ALL");

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

    public static Messages getInstance() {
        return instance;
    }
}
