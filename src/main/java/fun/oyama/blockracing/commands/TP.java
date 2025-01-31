package fun.oyama.blockracing.commands;

import fun.oyama.blockracing.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class TP implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!GameManager.gameStatus) {
            sender.sendMessage(ChatColor.DARK_RED + "游戏未开始！");
            return true;
        }

        // 红队TP
        if (GameManager.redTeamPlayer.contains((Player) sender)) {
            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[0])) && GameManager.redTeamPlayer.contains(Bukkit.getPlayer(args[0]))) {
                ((Player) sender).teleport(Objects.requireNonNull(Bukkit.getPlayer(args[0])));
                sender.sendMessage(ChatColor.RED + "[红队TP]" + ChatColor.WHITE + "已成功TP至" + Bukkit.getPlayer(args[0]).getName());
                return true;
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "玩家不存在或不在同一队伍！");
                return true;
            }
        }

        // 蓝队TP
        if (GameManager.blueTeamPlayer.contains((Player) sender)) {
            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[0])) && GameManager.blueTeamPlayer.contains(Bukkit.getPlayer(args[0]))) {
                ((Player) sender).teleport(Objects.requireNonNull(Bukkit.getPlayer(args[0])));
                sender.sendMessage(ChatColor.BLUE + "[蓝队TP]" + ChatColor.WHITE + "已成功TP至" + Bukkit.getPlayer(args[0]).getName());
                return true;
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "玩家不存在或不在同一队伍！");
                return true;
            }
        }

        if (GameManager.blueTeamPlayer.contains((Player) sender) || GameManager.redTeamPlayer.contains((Player) sender)){
            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[0])) && GameManager.blueTeamPlayer.contains(Bukkit.getPlayer(args[0])) || GameManager.redTeamPlayer.contains(Bukkit.getPlayer(args[0]))) {
                ((Player) sender).teleport(Objects.requireNonNull(Bukkit.getPlayer(args[0])));
                sender.sendMessage(ChatColor.BLUE + "[蓝队TP]" + ChatColor.WHITE + "已成功TP至" + Bukkit.getPlayer(args[0]).getName());
                return true;
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "玩家不存在或不在同一队伍！");
                return true;
            }

        }
        return true;
    }
}
