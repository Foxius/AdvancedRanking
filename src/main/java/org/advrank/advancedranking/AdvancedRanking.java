package org.advrank.advancedranking;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Objects;


public final class AdvancedRanking extends JavaPlugin {

    private Connection connection;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        copyArchive();
        int pluginId = 22915;
        new Metrics(this, pluginId);

        try {
            connectDatabase();
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(getCommand("ranking")).setExecutor(new RankingCommand(this));
        Objects.requireNonNull(getCommand("profile")).setExecutor(new ProfileCommand(this));
        Objects.requireNonNull(getCommand("setrank")).setExecutor(new SetRankCommand(this));
        getServer().getPluginManager().registerEvents(new RankingGUI(this), this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new RankingPlaceholder(this).register();
        }

        Objects.requireNonNull(getCommand("ranking")).setExecutor(new RankingCommand(this));
        getServer().getPluginManager().registerEvents(new RankingGUI(this), this);
        String version = getDescription().getVersion();
        String author = getDescription().getAuthors().toString();
        String site = getDescription().getWebsite();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.GREEN + "Плагин загружен!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.AQUA + "Версия: " + version);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.AQUA + "Автор: " + author + " ("+ChatColor.UNDERLINE + site + ")");

    }

    private void copyResource(File destination) throws IOException {
        try (InputStream in = getResource("AdvancedRanking.zip");
             OutputStream out = new FileOutputStream(destination)) {
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + "AdvancedRanking.zip");
            }
            IOUtils.copy(in, out);
        }
    }

    private void copyArchive() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File archive = new File(dataFolder, "AdvancedRanking.zip");
        if (!archive.exists()) {
            try {
                copyResource(archive);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Плагин отключен.");
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void connectDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + getDataFolder().getAbsolutePath() + "/ratings.db");
    }

    private void createTables() throws SQLException {
        String createRatingsTableSQL = "CREATE TABLE IF NOT EXISTS ratings ("
                + "uuid TEXT PRIMARY KEY,"
                + "rating INTEGER DEFAULT 0"
                + ");";
        try (PreparedStatement ps = connection.prepareStatement(createRatingsTableSQL)) {
            ps.execute();
        }

        String createCooldownsTableSQL = "CREATE TABLE IF NOT EXISTS cooldowns ("
                + "changer_uuid TEXT,"
                + "target_uuid TEXT,"
                + "last_changed TIMESTAMP,"
                + "PRIMARY KEY (changer_uuid, target_uuid)"
                + ");";
        try (PreparedStatement ps = connection.prepareStatement(createCooldownsTableSQL)) {
            ps.execute();
        }
    }


    public int getRating(Player player) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT rating FROM ratings WHERE uuid = ?")) {
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setRating(Player changer, Player target, int rating) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO ratings (uuid, rating) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET rating = ?")) {
            ps.setString(1, target.getUniqueId().toString());
            ps.setInt(2, rating);
            ps.setInt(3, rating);
            ps.executeUpdate();

            try (PreparedStatement psCooldown = connection.prepareStatement(
                    "INSERT INTO cooldowns (changer_uuid, target_uuid, last_changed) VALUES (?, ?, CURRENT_TIMESTAMP) ON CONFLICT(changer_uuid, target_uuid) DO UPDATE SET last_changed = CURRENT_TIMESTAMP")) {
                psCooldown.setString(1, changer.getUniqueId().toString());
                psCooldown.setString(2, target.getUniqueId().toString());
                psCooldown.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean canChangeRating(Player changer, Player target) {
        int cooldownMinutes = getConfig().getInt("cooldown_minutes", 1440);
        try (PreparedStatement ps = connection.prepareStatement("SELECT last_changed FROM cooldowns WHERE changer_uuid = ? AND target_uuid = ?")) {
            ps.setString(1, changer.getUniqueId().toString());
            ps.setString(2, target.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                LocalDateTime lastChanged = rs.getTimestamp("last_changed").toLocalDateTime();
                LocalDateTime now = LocalDateTime.now();
                return lastChanged.plusMinutes(cooldownMinutes).isBefore(now);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }


    public void increaseRating(Player changer, Player target) {
        int currentRating = getRating(target);
        setRating(changer, target, currentRating + 1);
    }

    public void decreaseRating(Player changer, Player target) {
        int currentRating = getRating(target);
        setRating(changer, target, currentRating - 1);
    }

    public void openRatingGUIForPlayer(Player target, Player changer) {
        boolean useResourcePack = getConfig().getBoolean("use_resourcepack", false);
        String title = useResourcePack ? ChatColor.WHITE + "\uF003Ƣ ": "Изменение рейтинга " + target.getName();
        Inventory gui = getServer().createInventory(null, 27, title);

        ItemStack playerHead = getPlayerHead(target);
        ItemStack increaseButton;
        ItemStack decreaseButton;

        if (useResourcePack) {
            increaseButton = createButton(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + getConfig().getString("buttons.increase"), 7637001);
            decreaseButton = createButton(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + getConfig().getString("buttons.decrease"), 7637002);
        } else {
            increaseButton = createButton(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + getConfig().getString("buttons.increase"), 0);
            decreaseButton = createButton(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + getConfig().getString("buttons.decrease"), 0);
        }

        gui.setItem(11, increaseButton);
        gui.setItem(13, playerHead);
        gui.setItem(15, decreaseButton);

        if (!useResourcePack) {
            ItemStack filler = createButton(Material.GRAY_STAINED_GLASS_PANE, "", 0);
            for (int i = 0; i < gui.getSize(); i++) {
                if (gui.getItem(i) == null) {
                    gui.setItem(i, filler);
                }
            }
        }

        changer.openInventory(gui);
    }




    private ItemStack createButton(Material material, String name, int customModelData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        if (customModelData != 0) {
            meta.setCustomModelData(customModelData);
        }
        item.setItemMeta(meta);
        return item;
    }


    private ItemStack getPlayerHead(Player player) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(player);
        meta.setDisplayName(player.getName());

        int rating = getRating(player);
        ChatColor ratingColor = rating >= 0 ? ChatColor.GREEN : ChatColor.RED;

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Рейтинг - " + ratingColor + rating);
        meta.setLore(lore);

        playerHead.setItemMeta(meta);
        return playerHead;
    }



}
