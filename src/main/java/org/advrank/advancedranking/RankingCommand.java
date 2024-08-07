package org.advrank.advancedranking;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


class RankingCommand implements CommandExecutor {
    private final AdvancedRanking plugin;

    public RankingCommand(AdvancedRanking plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только игроки могут использовать эту команду.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Использование: /ranking <игрок>");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Игрок не найден.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(ChatColor.GOLD + "[AdvancedRanking] " + ChatColor.RED + "Вы не можете изменять свой собственный рейтинг.");
            return true;
        }

        plugin.openRatingGUIForPlayer(target, player);
        return true;
    }

}
