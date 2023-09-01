package fun.oyama.blockracing.utils;

import fun.oyama.blockracing.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownRestart {

    public static void countdownRestart(int sec) {

//        int x = 1;
//        Bukkit.getLogger().info(sec + " 秒后关闭服务器");
        Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(sec) + " 秒后关闭服务器");
        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getServer().shutdown();

            }
        }.runTaskLater(Main.getInstance(),sec*20L);
    }


}
