package fun.oyama.blockracing.managers;

import fun.oyama.blockracing.listeners.playerClickEvent;
import fun.oyama.blockracing.utils.Color;
import fun.oyama.blockracing.utils.PotionEffectUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import fun.oyama.blockracing.Main;
import fun.oyama.blockracing.utils.ItemBuilder;

import java.util.*;

import static fun.oyama.blockracing.listeners.messageSendPlayerClickEvent.editAmountPlayer;
import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getServer;

public class GameManager {
    public static ArrayList<String> redCurrentBlocks = new ArrayList<>();
    public static ArrayList<String> blueCurrentBlocks = new ArrayList<>();
    public static ArrayList<String> redTeamBlocks = new ArrayList<>();
    public static ArrayList<String> blueTeamBlocks = new ArrayList<>();
    public static ArrayList<Player> redTeamPlayer = new ArrayList<>();
    public static ArrayList<Player> blueTeamPlayer = new ArrayList<>();
    public static ArrayList<Player> inGamePlayer = new ArrayList<>();
//    public static ArrayList<Player> inGamePlayer = new ArrayList<>();
    public static boolean gameStatus = false;
    public static boolean gameOver = false;
    public static String winnerTeamDisplayname;
    public static int locateCost;
    public static boolean extremeMode = false;
    static Random r = new Random();

    static List<World> worldLists = Bukkit.getWorlds();

    // 玩家登录时的设置
    public static void playerLogin(Player player) {
        if (!gameStatus) {
            player.setGameMode(GameMode.ADVENTURE);
            player.sendMessage(Color.format("&a欢迎来到方块竞速，按下[蹲下]+[副手]打开菜单"));
        } else {
            if (playerClickEvent.redTeamPlayerString.contains(player.getName())) {
                if (!redTeamPlayer.contains(player)) {
                    redTeamPlayer.add(player);
                    blueTeamPlayer.remove(player);
                }
            } else if (playerClickEvent.blueTeamPlayerString.contains(player.getName())) {
                if (!blueTeamPlayer.contains(player)) {
                    blueTeamPlayer.add(player);
                    redTeamPlayer.remove(player);
                }
            } else {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(ChatColor.RED + "游戏已开始，你现在是旁观者！");
            }
        }
    }

    // 游戏开始时的设置
    public static void gameStart() {
        gameStatus = true;
        editAmountPlayer.clear();
        if (!extremeMode) setBlocks();
        else setExtremeBlocks();

        // 检查方块库是否正常
//        boolean flag = false;
        for (String s : BlockManager.blocks) {
            try {
                ItemStack stack = new ItemStack(Material.valueOf(s));
                ItemBuilder TeamChestBuilder = new ItemBuilder(stack);
                TeamChestBuilder.setAmount(64);
                TeamChestBuilder.toItemStack();
                InventoryManager.settings.setItem(0, stack);
            } catch (Exception e) {
                Bukkit.getLogger().severe("名为 " + s + " 的物品不存在！请检查配置文件！");
                Bukkit.broadcastMessage("物品"+ s + "不存在，请检查配置文件");
//                flag = true;
                getServer().getPluginManager().disablePlugin(Main.getInstance());
            }
        }
//        if (flag) {
//            getServer().getPluginManager().disablePlugin(Main.getInstance());
//            return;
//        }

        if (playerClickEvent.blockAmount <= 20) locateCost = 2;
        else if (playerClickEvent.blockAmount <= 50) locateCost = 3;
        else if (playerClickEvent.blockAmount <= 100) locateCost = 5;
        else if (playerClickEvent.blockAmount <= 200) locateCost = 8;
        else locateCost = 10;
        InventoryManager.setLocateItem();

        /*!!!!重要!!!!
        * 注册名为GameTick的计时器 每5Tick执行一次
        * */
        BukkitTask gameTick = new GameTick().runTaskTimer(Main.getInstance(), 1L, 5L);


        // 更新计分板
        ScoreboardManager.update();

        // 未选队玩家（旁观者）处理
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!playerClickEvent.prepareList.contains(player)) {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(ChatColor.RED + "游戏已开始，您现在是旁观者！");
                inGamePlayer.remove(player);
            }
        }

        // 随机传送
//        World playerWorld = Bukkit.getWorld("world");
//        for (Player p : inGamePlayer) {
//            double randX = r.nextInt(20000) - 10000;
//            double randZ = r.nextInt(20000) - 10000;
//            Location offset = new Location(playerWorld, randX, 0, randZ).toHighestLocation();
//            double Y = offset.getY() + 1;
//            offset.setY(Y);
//            p.teleport(offset);
//            p.sendMessage(ChatColor.GREEN + "已传送到 " + offset.getX() + " " + offset.getY() + " " + offset.getZ());
//        }




        // 设置生命值、经验、等级、饱食度 移除所有玩家效果 设置模式为生存 给予玩家永久抗火、水下呼吸 清除物品栏 随机传送
        World playerWorld = Bukkit.getWorld("world");
        for (Player p : inGamePlayer) {
            p.setHealth(20);
            p.setExp(0);
            p.setLevel(0);
            p.setFoodLevel(20);
            p.setSaturation(10);
            for (PotionEffect effect : p.getActivePotionEffects())
                p.removePotionEffect(effect.getType());
            p.setGameMode(GameMode.SURVIVAL);
            PotionEffectUtils.add(p, PotionEffectType.FIRE_RESISTANCE, 999999, 1);
            PotionEffectUtils.add(p, PotionEffectType.WATER_BREATHING, 999999, 1);
            p.getInventory().clear();

            //随机传送
            double randX = r.nextInt(20000) - 10000;
            double randZ = r.nextInt(20000) - 10000;
            Location offset = new Location(playerWorld, randX, 0, randZ).toHighestLocation();
            double Y = offset.getY() + 1;
            offset.setY(Y);
            p.teleport(offset);
            p.sendMessage(ChatColor.GREEN + "已传送到 " + offset.getX() + " " + offset.getY() + " " + offset.getZ());
        }

        // 设置难度为简单、时间设置为1000
        for (World worldList : worldLists) {
//            Bukkit.getLogger().info(String.valueOf(Bukkit.getWorld(worldList.getName())));
            Objects.requireNonNull(Bukkit.getWorld(worldList.getName())).setDifficulty(Difficulty.EASY);
            Objects.requireNonNull(Bukkit.getWorld(worldList.getName())).setTime(1000);
        }

        for (Player p : redTeamPlayer) {
            if (!inGamePlayer.contains(p)) {
                redTeamPlayer.remove(p.getPlayer());
                playerClickEvent.redTeamPlayerString.remove(Objects.requireNonNull(p.getPlayer()).getName());
                ScoreboardManager.red.removeEntry(p.getName());
            }
        }
        for (Player p : blueTeamPlayer) {
            if (!inGamePlayer.contains(p)) {
                blueTeamPlayer.remove(p.getPlayer());
                playerClickEvent.blueTeamPlayerString.remove(Objects.requireNonNull(p.getPlayer()).getName());
                ScoreboardManager.blue.removeEntry(p.getName());
            }
        }
        Bukkit.getLogger().info("红队本局全部方块：");
        Bukkit.getLogger().info(redCurrentBlocks.toString() + redTeamBlocks.toString());
        Bukkit.getLogger().info("蓝队本局全部方块：");
        Bukkit.getLogger().info(blueCurrentBlocks.toString() + blueTeamBlocks.toString());
    }

    // 设置两个队伍的目标方块
    private static void setBlocks() {
        ArrayList<String> blocks_temp = new ArrayList<>();
        Collections.addAll(blocks_temp, BlockManager.blocks);
        for (int i = 0; i < playerClickEvent.blockAmount; i++) {
            int a = r.nextInt(BlockManager.blocks.length - i);
            redTeamBlocks.add(blocks_temp.get(a));
            blocks_temp.remove(a);
        }
        blocks_temp.clear();
        Collections.addAll(blocks_temp, BlockManager.blocks);
        for (int i = 0; i < playerClickEvent.blockAmount; i++) {
            int a = r.nextInt(BlockManager.blocks.length - i);
            blueTeamBlocks.add(blocks_temp.get(a));
            blocks_temp.remove(a);
        }
        setCurrentBlocks();
    }

    private static void setCurrentBlocks() {
        redCurrentBlocks.add(redTeamBlocks.get(0));
        redCurrentBlocks.add(redTeamBlocks.get(1));
        redCurrentBlocks.add(redTeamBlocks.get(2));
        redCurrentBlocks.add(redTeamBlocks.get(3));
        blueCurrentBlocks.add(blueTeamBlocks.get(0));
        blueCurrentBlocks.add(blueTeamBlocks.get(1));
        blueCurrentBlocks.add(blueTeamBlocks.get(2));
        blueCurrentBlocks.add(blueTeamBlocks.get(3));
        blueTeamBlocks.remove(0);
        blueTeamBlocks.remove(0);
        blueTeamBlocks.remove(0);
        blueTeamBlocks.remove(0);
        redTeamBlocks.remove(0);
        redTeamBlocks.remove(0);
        redTeamBlocks.remove(0);
        redTeamBlocks.remove(0);
    }

    private static void setExtremeBlocks() {
        ArrayList<String> blocks_temp = new ArrayList<>();
        Collections.addAll(blocks_temp, BlockManager.blocks);
        for (int i = 0; i < playerClickEvent.blockAmount; i++) {
            int a = r.nextInt(BlockManager.blocks.length - i);
            redTeamBlocks.add(blocks_temp.get(a));
            blueTeamBlocks.add(blocks_temp.get(a));
            blocks_temp.remove(a);
        }
        setCurrentBlocks();
    }

    // 胜利检测
//    public static void redWin() {
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            player.closeInventory();
//        }
//        ConsoleCommandHandler.send("tellraw @a \"\\u00a7c\\u00a7l红队获胜！\"");
//        ConsoleCommandHandler.send("title @a title \"\\u00a7c\\u00a7l红队获胜！\"");
//        ConsoleCommandHandler.send("gamemode spectator @a");
//        ConsoleCommandHandler.send("execute as @a at @s run playsound minecraft:ui.toast.challenge_complete player @s");
//
//
//    }
//
//    public static void blueWin() {
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            player.closeInventory();
//        }
//        ConsoleCommandHandler.send("tellraw @a \"\\u00a79\\u00a7l蓝队获胜！\"");
//        ConsoleCommandHandler.send("title @a title \"\\u00a79\\u00a7l蓝队获胜！\"");
//        ConsoleCommandHandler.send("gamemode spectator @a");
//        ConsoleCommandHandler.send("execute as @a at @s run playsound minecraft:ui.toast.challenge_complete player @s");
//    }

    public static void setWinner(String teamName,Boolean termination) {
        /*
          设置本场游戏的赢家
         
          @param teamName 队伍名称，blue==蓝队 red==红队
          @param termination 是否为终止游戏 默认应为false
         */

        switch (teamName){
            case "blue" -> winnerTeamDisplayname = "蓝队";
            case "red" -> winnerTeamDisplayname = "红队";
            default -> {
                winnerTeamDisplayname = null;
                break;
            }
        }

        GameManager.gameStatus = false;
        GameManager.gameOver = true;
        ScoreboardManager.update();
        if (termination = true ){
            Bukkit.broadcastMessage(ChatColor.RED+"游戏提前终止！管理员设置最终赢家为"+ GameManager.winnerTeamDisplayname);
        } else {
            Bukkit.broadcastMessage(ChatColor.GREEN+ "恭喜"+ winnerTeamDisplayname + "获胜!");
        }

        for (Player player : Bukkit.getOnlinePlayers()) {

            player.closeInventory();
            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
            player.teleport(new Location(Bukkit.getServer().getWorld("world"), 0, 80, 0));

        }
//        ConsoleCommandHandler.send("tellraw @a \"\\u00a79\\u00a7l蓝队获胜！\"");
//        ConsoleCommandHandler.send("title @a title \"\\u00a79\\u00a7l蓝队获胜！\"");
//        ConsoleCommandHandler.send("gamemode spectator @a");
//        ConsoleCommandHandler.send("execute as @a at @s run playsound minecraft:ui.toast.challenge_complete player @s");
    }
}
