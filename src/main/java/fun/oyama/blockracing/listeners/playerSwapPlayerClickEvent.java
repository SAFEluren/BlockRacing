package fun.oyama.blockracing.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import static fun.oyama.blockracing.managers.GameManager.gameStatus;
import static fun.oyama.blockracing.managers.InventoryManager.menu;
import static fun.oyama.blockracing.managers.InventoryManager.settings;

public class playerSwapPlayerClickEvent implements Listener {
    @EventHandler
    public void onPlayerSwap(PlayerSwapHandItemsEvent e) {
        if (e.getPlayer().isSneaking()) {
            if (!gameStatus) e.getPlayer().openInventory(settings);
            else e.getPlayer().openInventory(menu);
            e.setCancelled(true);
        }
    }
}
