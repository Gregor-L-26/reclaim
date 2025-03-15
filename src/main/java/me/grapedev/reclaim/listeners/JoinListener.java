package me.grapedev.reclaim.listeners;

import me.grapedev.reclaim.database.ReclaimDatabase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ReclaimDatabase.getReclaimStatusAsync(event.getPlayer().getUniqueId(), status -> {
            ReclaimDatabase.setReclaimStatusAsync(event.getPlayer().getUniqueId(), status);
        });
    }
}
