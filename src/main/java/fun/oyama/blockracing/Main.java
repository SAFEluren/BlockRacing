package fun.oyama.blockracing;

import com.destroystokyo.paper.profile.PlayerProfile;
import fun.oyama.blockracing.commands.Locate;
import fun.oyama.blockracing.commands.TP;
import fun.oyama.blockracing.commands.debugCommands;
import fun.oyama.blockracing.listeners.*;
import fun.oyama.blockracing.managers.ConfigManager;
import fun.oyama.blockracing.managers.InventoryManager;
import fun.oyama.blockracing.managers.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class Main extends JavaPlugin {
    public static int OnlinePlayer = Bukkit.getOnlinePlayers().size();
    private static Main instance;
    ConfigManager configManager;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

//        TitleManagerAPI api = (TitleManagerAPI) Bukkit.getServer().getPluginManager().getPlugin("TitleManager");

        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(new playerClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new playerLoginPlayerClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new playerQuitPlayerClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new messageSendPlayerClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new playerSwapPlayerClickEvent(), this);

        // 注册命令处理器

        Objects.requireNonNull(Bukkit.getPluginCommand("locate")).setExecutor(new Locate());
        Objects.requireNonNull(Bukkit.getPluginCommand("locate")).setTabCompleter(new Locate());
        Objects.requireNonNull(Bukkit.getPluginCommand("tp")).setExecutor(new TP());
        Objects.requireNonNull(Bukkit.getPluginCommand("brdebug")).setExecutor(new debugCommands());


        // 初始化记分板
        ScoreboardManager.createScoreboard();
        ScoreboardManager.setPreScoreboard();

        // 创建资源文件
        if (!getDataFolder().exists()) {
            this.saveResource("EasyBlocks.txt", false);
            this.saveResource("NormalBlocks.txt", false);
            this.saveResource("HardBlocks.txt", false);
            this.saveResource("DyedBlocks.txt", false);
            this.saveResource("EndBlocks.txt", false);
            this.saveResource("zh_cn.json", false);
        }

        try {
            configManager = new ConfigManager();
        } catch (IOException e) {
            Bukkit.getLogger().warning(e.toString());
        }

        // 初始化菜单
        PlayerProfile redProfile = Bukkit.createProfile("InventoryHolder");
        InventoryManager.init();

        // 主类实例化5tick后，遍历世界列表，输出、设置死亡不掉落、难度为和平
        List<World> worldLists = Bukkit.getWorlds();

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            StringBuilder loadedWorld = new StringBuilder(256);
            Bukkit.getLogger().info("=================");
            for (World worldList : worldLists) {
                loadedWorld.append(loadedWorld.isEmpty() ? "" : "\n").append("世界名称：").append(worldList.getName()).append(",种子：").append(worldList.getSeed());

                Objects.requireNonNull(Bukkit.getWorld(worldList.getName())).setGameRule(GameRule.KEEP_INVENTORY, true);
                Objects.requireNonNull(Bukkit.getWorld(worldList.getName())).setDifficulty(Difficulty.PEACEFUL);
            }
            Bukkit.getLogger().info("已加载的世界:");
            Bukkit.getLogger().info(String.valueOf(loadedWorld));
            Bukkit.getLogger().info("=================");
        }, 5);
    }

}
