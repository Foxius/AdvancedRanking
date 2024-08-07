package org.advrank.advancedranking;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfileCommand implements CommandExecutor {

    private final AdvancedRanking plugin;

    public ProfileCommand(AdvancedRanking plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int rating = plugin.getRating(player);
            ChatColor ratingColor = rating >= 0 ? ChatColor.GREEN : ChatColor.RED;
            player.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.AQUA + "Ваш текущий рейтинг: " + ratingColor + rating);
        } else {
            sender.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Эту команду могут использовать только игроки.");
        }
        return true;
    }
}
