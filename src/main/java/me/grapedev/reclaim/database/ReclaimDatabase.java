package me.grapedev.reclaim.database;

import me.grapedev.reclaim.Reclaim;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.UUID;
import java.util.function.Consumer;

public class ReclaimDatabase {
    private static Reclaim plugin = null;
    private static Connection connection;

    public ReclaimDatabase(Reclaim plugin) {
        this.plugin = plugin;
        connect();
        createTable();
    }

    private void connect() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/players.db";
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[PlayerDatabase] Could not connect to database: " + e.getMessage());
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS players (" +
                "uuid TEXT PRIMARY KEY, " +
                "reclaim_status BOOLEAN NOT NULL DEFAULT 1)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[Reclaim] Could not create table: " + e.getMessage());
        }
    }

    public static void setReclaimStatusAsync(UUID uuid, boolean status) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO players (uuid, reclaim_status) VALUES (?, ?) " +
                    "ON CONFLICT(uuid) DO UPDATE SET reclaim_status = excluded.reclaim_status;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, uuid.toString());
                pstmt.setBoolean(2, status);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Reclaim] Could not update reclaim status: " + e.getMessage());
            }
        });
    }

    public static void resetReclaimStatusAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "UPDATE players SET reclaim_status = 0";
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Reclaim] Could not reset reclaim status: " + e.getMessage());
            }
        });
    }

    public static void getReclaimStatusAsync(UUID uuid, Consumer<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "SELECT reclaim_status FROM players WHERE uuid = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, uuid.toString());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    callback.accept(rs.getBoolean("reclaim_status"));
                    return;
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Reclaim] Could not retrieve reclaim status: " + e.getMessage());
            }
            callback.accept(false); // Default to false if not found
        });
    }

    public static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[Reclaim] Could not close database connection: " + e.getMessage());
        }
    }
}
