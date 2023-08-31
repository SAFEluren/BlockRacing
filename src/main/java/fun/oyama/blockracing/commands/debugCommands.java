package fun.oyama.blockracing.commands;

import fun.oyama.blockracing.managers.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class debugCommands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && args.length >=1) {
            switch (args[0]) {
                case "setWinner" -> {
                    if (!(args.length == 1) && GameManager.gameStatus) {
                        GameManager.setWinner(args[1], true);
                    } else {
                        sender.sendMessage(ChatColor.RED + "错误的用法或游戏未开始");
                    }
                }
                default -> sender.sendMessage(ChatColor.RED + "错误的用法");
            }
        } else {
            if (args.length==0){
                sender.sendMessage(ChatColor.RED + "错误的用法");
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            switch (args.length) {
                case 1 -> {
                    return Arrays.asList("setWinner");
                }
                case 2 -> {
                    return Arrays.asList("blue", "red");
                }
                default -> List.of("");
            }

        }

        return null;
    }
}
