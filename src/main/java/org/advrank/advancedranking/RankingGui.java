package org.advrank.advancedranking;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

class RankingGUI implements Listener {
    private final AdvancedRanking plugin;
    private final Logger logger;

    public RankingGUI(AdvancedRanking plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player changer = (Player) event.getWhoClicked();
        Inventory gui = event.getInventory();

        boolean useResourcePack = plugin.getConfig().getBoolean("use_resourcepack", false);
        String expectedTitle = useResourcePack ? ChatColor.WHITE + "\uF003Ƣ " : "Изменение рейтинга для ";
        if (!event.getView().getTitle().startsWith(expectedTitle)) return;

        event.setCancelled(true);

        if (event.getClick() != ClickType.LEFT) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        String targetName = event.getView().getTitle().substring(expectedTitle.length()).trim();
        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            changer.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Игрок не найден.");
            return;
        }

        if (!plugin.canChangeRating(changer, target)) {
            changer.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Вы не можете изменять рейтинг этого игрока чаще, чем раз в " + plugin.getConfig().getInt("cooldown_minutes", 1440) + " минут.");
            return;
        }

        String changerName = changer.getName();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);

        if (useResourcePack) {
            if (clickedItem.getType() == Material.GREEN_STAINED_GLASS_PANE && clickedItem.getItemMeta().getCustomModelData() == 7637001) {
                plugin.increaseRating(changer, target);
                changer.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.GREEN + "Рейтинг " + target.getName() + " увеличен!");
                target.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.GREEN + "Ваш рейтинг увеличен " + changerName + "!");
                plugin.getLogger().log(Level.INFO, changerName + " увеличил рейтинг " + target.getName() + " в " + formattedNow);
            } else if (clickedItem.getType() == Material.RED_STAINED_GLASS_PANE && clickedItem.getItemMeta().getCustomModelData() == 7637002) {
                plugin.decreaseRating(changer, target);
                changer.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Рейтинг " + target.getName() + " уменьшен!");
                target.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Ваш рейтинг уменьшен " + changerName + "!");
                plugin.getLogger().log(Level.INFO, changerName + " уменьшил рейтинг " + target.getName() + " в " + formattedNow);
            }
        } else {
            if (clickedItem.getType() == Material.GREEN_STAINED_GLASS_PANE) {
                plugin.increaseRating(changer, target);
                changer.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.GREEN + "Рейтинг " + target.getName() + " увеличен!");
                target.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.GREEN + "Ваш рейтинг увеличен " + changerName + "!");
                plugin.getLogger().log(Level.INFO, changerName + " увеличил рейтинг " + target.getName() + " в " + formattedNow);
            } else if (clickedItem.getType() == Material.RED_STAINED_GLASS_PANE) {
                plugin.decreaseRating(changer, target);
                changer.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Рейтинг " + target.getName() + " уменьшен!");
                target.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Ваш рейтинг уменьшен " + changerName + "!");
                plugin.getLogger().log(Level.INFO, changerName + " уменьшил рейтинг " + target.getName() + " в " + formattedNow);
            }
        }

        changer.closeInventory();
    }

}