package org.advrank.advancedranking;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetRankCommand implements CommandExecutor {

    private final AdvancedRanking plugin;

    public SetRankCommand(AdvancedRanking plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("advancedranking.setrank")) {
            sender.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "У вас нет прав на выполнение этой команды.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Использование: /setrank <игрок> <ранг>");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Игрок не найден.");
            return true;
        }

        int rank;
        try {
            rank = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Ранг должен быть числом.");
            return true;
        }

        plugin.setRating((Player) sender, target, rank);
        sender.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.GREEN + "Ранг " + target.getName() + " установлен на " + rank);
        target.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.GREEN + "Ваш ранг был установлен на " + rank + " игроком " + sender.getName());

        return true;
    }
}
