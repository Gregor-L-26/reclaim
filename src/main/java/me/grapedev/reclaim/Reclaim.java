package me.grapedev.reclaim;

import me.grapedev.reclaim.commands.ReclaimCommand;
import me.grapedev.reclaim.config.Config;
import me.grapedev.reclaim.config.Messages;
import me.grapedev.reclaim.database.ReclaimDatabase;
import me.grapedev.reclaim.listeners.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Reclaim extends JavaPlugin {

    private static JavaPlugin instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        new ReclaimDatabase(this);
        Messages.getInstance().load();
        Config.getInstance().load();
        getServer().getPluginManager().registerEvents(new JoinListener(), this);

        getCommand("reclaim").setExecutor(new ReclaimCommand());


        getLogger().info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}
