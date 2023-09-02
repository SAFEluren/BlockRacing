package fun.oyama.blockracing.listeners;

import fun.oyama.blockracing.managers.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;

import static fun.oyama.blockracing.managers.GameManager.gameStatus;


public class messageSendPlayerEvent implements Listener {
    public static ArrayList<Player> editAmountPlayer = new ArrayList<>();
    boolean flag = false;
    @EventHandler
    public void onMessageSend(AsyncPlayerChatEvent e) {
        if (gameStatus) return;
        if (editAmountPlayer.contains(e.getPlayer())) {
            if (e.getMessage().equals("quit")) {
                e.getPlayer().sendMessage(ChatColor.GREEN + "成功退出输入模式！");
                editAmountPlayer.remove(e.getPlayer());
                e.setCancelled(true);
                return;
            }
            try {
                playerClickEvent.blockAmount = Integer.parseInt(e.getMessage());
                flag = true;
            } catch (Exception ex) {
                e.getPlayer().sendMessage(ChatColor.RED + "请输入正确数字！如果您想退出输入模式，请发送quit");
                flag = false;
            } finally {
                e.setCancelled(true);
            }
            if (flag) {
                if (playerClickEvent.blockAmount < 10) {
//                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> ConsoleCommandHandler.send("tellraw @a {\"color\": \"green\",\"text\": \"需要收集的方块数量更改为10\"}"), 1L);
                    e.getPlayer().sendMessage(ChatColor.GREEN+ "目标方块数量最小为10，自动修改为10");
                    playerClickEvent.blockAmount = 10;
                } else {
//                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> ConsoleCommandHandler.send("tellraw @a {\"color\": \"green\",\"text\": \"将需要收集的方块数量更改为" + blockAmount + "\"}"), 1L);
                    Bukkit.broadcastMessage(ChatColor.GREEN+ "更改方块数量为 " + playerClickEvent.blockAmount);

                }
                editAmountPlayer.remove(e.getPlayer());
                ScoreboardManager.update();
            }
        }
    }

}
