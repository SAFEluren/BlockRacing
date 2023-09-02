package fun.oyama.blockracing.listeners;

import fun.oyama.blockracing.Main;
import fun.oyama.blockracing.managers.GameManager;
import fun.oyama.blockracing.managers.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class playerLoginPlayerEvent implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        // 设置记分板
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> ScoreboardManager.setPlayerScoreboard(e.getPlayer()), 40);
        // 初始化
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> GameManager.playerLogin(e.getPlayer()), 40);
        // 在线列表添加玩家
        GameManager.inGamePlayer.add(e.getPlayer());


    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    World spawnWorld = Bukkit.getWorld("spawn");
                    if (!GameManager.gameStatus){
                        if (spawnWorld != null) {
                            e.getPlayer().teleport(spawnWorld.getSpawnLocation());
                        }
                    }
                } catch (NullPointerException ignored){}
            }
        }.runTaskLater(Main.getInstance(),10L);


    }

}
