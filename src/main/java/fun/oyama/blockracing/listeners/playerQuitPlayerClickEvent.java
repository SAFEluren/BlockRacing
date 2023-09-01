package fun.oyama.blockracing.listeners;

import fun.oyama.blockracing.managers.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class playerQuitPlayerClickEvent implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // 在线列表移除玩家
        GameManager.inGamePlayer.remove(e.getPlayer());
        playerClickEvent.prepareList.remove(e.getPlayer());
        messageSendPlayerClickEvent.editAmountPlayer.remove(e.getPlayer());

    }

}
