package me.grapedev.reclaim.commands;

import me.grapedev.reclaim.Reclaim;
import me.grapedev.reclaim.config.Config;
import me.grapedev.reclaim.config.Messages;
import me.grapedev.reclaim.database.ReclaimDatabase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("All")
public class ReclaimCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        // Check players rank by checking if they have permission, run console command for the rank
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "reset":
                    if (args[1].equalsIgnoreCase("all")) {
                        if (sender.hasPermission("reclaim.reset")) {
                            ReclaimDatabase.resetReclaimStatusAsync();
                            sender.sendMessage(Messages.RESET_RECLAIM_ALL);
                            return true;
                        }
                        sender.sendMessage(Messages.NO_PERMISSION);
                        return true;
                    }
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(Messages.PLAYER_NOT_FOUND);
                        return true;
                    }

                    if (sender.hasPermission("reclaim.reset")) {
                        ReclaimDatabase.setReclaimStatusAsync(target.getUniqueId(), false);
                        sender.sendMessage(Messages.RESET_RECLAIM);
                        return true;
                    }
                    sender.sendMessage(Messages.NO_PERMISSION);
                    return true;

                default:
                    sender.sendMessage(Messages.INVALID_RESET_USAGE);
                    return true;
            }
        }
        // check if sender is console
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.PLAYERS_ONLY);
            return true;
        }

        Player player = (Player) sender;
        if (args.length != 0) {
            sender.sendMessage(Messages.INVALID_RESET_USAGE);
            return true;
        }

        for (String rankKey : Config.getConfig().getConfigurationSection("Ranks").getKeys(false)) {
            if (player.hasPermission(Config.getConfig().getString("Ranks." + rankKey + ".Permission"))) {
                ReclaimDatabase.getReclaimStatusAsync(player.getUniqueId(), status -> {
                    if (status) {
                        player.sendMessage(Messages.ALREADY_CLAIMED);
                        return;
                    }
                    player.sendMessage(Messages.RECLAIM_SUCCESS);
                    ReclaimDatabase.setReclaimStatusAsync(player.getUniqueId(), true);
                    Bukkit.getScheduler().runTask(Reclaim.getInstance(), () -> {
                        Config.getConfig().getStringList("Ranks." + rankKey + ".Commands").forEach(command -> {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                        });
                    });

                });
                return true;
            }
        }
        player.sendMessage(Messages.NO_PERMISSION);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        // Tab completion for the command with starts with
        if (!sender.hasPermission("reclaim.reset")) {
            return List.of();
        }
        if (args.length == 1) {
            return List.of("reset");
        }
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList().stream().filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        }


        return List.of();
    }
}