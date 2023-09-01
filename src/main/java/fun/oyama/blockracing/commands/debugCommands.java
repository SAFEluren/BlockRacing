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

import static fun.oyama.blockracing.utils.CountdownRestart.countdownRestart;

public class debugCommands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && args.length >= 1) {
            switch (args[0]) {
                case "setWinner" -> {
                    if ((args.length == 2) && GameManager.gameStatus) {
                        GameManager.setWinner(args[1], true);
                    } else {
                        sender.sendMessage(ChatColor.RED + "错误的用法或游戏未开始");
                    }
                }
                case "stopServer" -> {
                    if ((args.length == 2)) {
                        try {
                            int sec = Integer.parseInt(args[1]);
                            countdownRestart(sec);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "错误的用法");
                            return false;
                        }

                    }
                }
                default -> sender.sendMessage(ChatColor.RED + "错误的用法");
            }
        } else {
            if (args.length == 0) {
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
                    return Arrays.asList("setWinner", "stopServer");
                }
                case 2 -> {
                    switch (args[1]) {
                        case "setWinner" -> {
                            return Arrays.asList("blue", "red");
                        }
                        case "stopServer" -> {
                            return List.of("Seconds");
                        }
                    }
                }
                default -> List.of();
            }

        }

        return null;
    }
}
