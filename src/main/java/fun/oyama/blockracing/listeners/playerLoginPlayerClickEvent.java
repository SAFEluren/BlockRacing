package fun.oyama.blockracing.listeners;

import fun.oyama.blockracing.Main;
import fun.oyama.blockracing.managers.GameManager;
import fun.oyama.blockracing.managers.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class playerLoginPlayerClickEvent implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        // 设置记分板
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> ScoreboardManager.setPlayerScoreboard(e.getPlayer()), 40);
        // 初始化
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> GameManager.playerLogin(e.getPlayer()), 40);
        // 在线列表添加玩家
        GameManager.inGamePlayer.add(e.getPlayer());

    }

}
