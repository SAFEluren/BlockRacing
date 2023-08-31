package fun.oyama.blockracing.managers;

import fun.oyama.blockracing.Main;
import fun.oyama.blockracing.listeners.EventListener;
import fun.oyama.blockracing.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;

public class ScoreboardManager {
    public static Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    public static Team red = scoreboard.registerNewTeam("red");
    public static Team blue = scoreboard.registerNewTeam("blue");
    public static Objective sidebar;
    public static int redTeamScore;
    public static int blueTeamScore;

    static ArrayList<String> easyBlock = new ArrayList<>();
    static ArrayList<String> normalBlock = new ArrayList<>();
    static ArrayList<String> hardBlock = new ArrayList<>();
    static ArrayList<String> dyedBlock = new ArrayList<>();
    static ArrayList<String> endBlock = new ArrayList<>();

    // 服务器第一次启动时的初始设置
    public static void createScoreboard() {
        red.setDisplayName(ChatColor.RED + "红队");
        red.setColor(ChatColor.RED);
        red.setPrefix(Color.format("&c[红队]"));
        red.addEntry("redPlayer");
        blue.setDisplayName(ChatColor.BLUE + "蓝队");
        blue.setColor(ChatColor.BLUE);
        blue.setPrefix(Color.format("&1[蓝队]"));
        blue.addEntry("bluePlayer");

        sidebar = scoreboard.registerNewObjective("sidebar", "dummy");
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (int i = 1; i <= 15; i++) {
            Team team = scoreboard.registerNewTeam("SLOT_" + i);
            team.addEntry(genEntry(i));
        }
    }


    // 游戏开始前准备阶段的记分板设置
    public static void setPreScoreboard() {
        setTitle("&b方块竞速");
        setSlot(6, ChatColor.GREEN + "Shift+F打开菜单");
        setSlot(5, "");
        setSlot(4, ChatColor.YELLOW + "当前模式：" + (GameManager.extremeMode ? "极限竞速模式" : "普通模式"));
        setSlot(3, ChatColor.YELLOW + "方块数量：" + EventListener.blockAmount);
        setSlot(2, ChatColor.YELLOW + "方块库：简单方块" + (EventListener.enableNormalBlock ? "+中等方块" : "") + (EventListener.enableHardBlock ? "+困难方块" : "") + (EventListener.enableDyedBlock ? "+染色方块" : "") + (EventListener.enableEndBlock ? "+末地方块" : ""));
        setSlot(1, "");
    }

    public static void setGameScoreboard() {
        Collections.addAll(easyBlock, BlockManager.easyBlocks);
        Collections.addAll(normalBlock, BlockManager.normalBlocks);
        Collections.addAll(hardBlock, BlockManager.hardBlocks);
        Collections.addAll(dyedBlock, BlockManager.dyedBlocks);
        Collections.addAll(endBlock, BlockManager.endBlocks);

        if (GameManager.redCurrentBlocks.size() >= 1) {
            setSlot(11, setDifficultyDisplay(GameManager.redCurrentBlocks.get(0)));
            setSlot(10, "");
            setSlot(9, "");
            setSlot(8, "");
        }
        if (GameManager.redCurrentBlocks.size() >= 2) {
            setSlot(10, setDifficultyDisplay(GameManager.redCurrentBlocks.get(1)));
        }
        if (GameManager.redCurrentBlocks.size() >= 3) {
            setSlot(9, setDifficultyDisplay(GameManager.redCurrentBlocks.get(2)));
        }
        if (GameManager.redCurrentBlocks.size() >= 4) {
            setSlot(8, setDifficultyDisplay(GameManager.redCurrentBlocks.get(3)));
        }
        if (GameManager.blueCurrentBlocks.size() >= 1) {
            setSlot(5, setDifficultyDisplay(GameManager.blueCurrentBlocks.get(0)));
            setSlot(4, "");
            setSlot(3, "");
            setSlot(2, "");
        }
        if (GameManager.blueCurrentBlocks.size() >= 2) {
            setSlot(4, setDifficultyDisplay(GameManager.blueCurrentBlocks.get(1)));
        }
        if (GameManager.blueCurrentBlocks.size() >= 3) {
            setSlot(3, setDifficultyDisplay(GameManager.blueCurrentBlocks.get(2)));
        }
        if (GameManager.blueCurrentBlocks.size() >= 4) {
            setSlot(2, setDifficultyDisplay(GameManager.blueCurrentBlocks.get(3)));
        }

        if (GameManager.extremeMode) setSlot(13, ChatColor.YELLOW + "当前游戏模式：极限竞速");
        setSlot(12, "&c红队：&e" + redTeamScore + "积分" + "\u00a7b  (" + GameTick.redCompleteAmount + "/" + EventListener.blockAmount + ")");
        setSlot(7, "-------------------");
        setSlot(6, "&9蓝队：&e" + blueTeamScore + "积分" + "\u00a7b  (" + GameTick.blueCompleteAmount + "/" + EventListener.blockAmount + ")");
        setSlot(1, "");
    }



    // 设置玩家记分板
    public static void setPlayerScoreboard(Player p) {
        p.setScoreboard(scoreboard);
    }
    public static void setGameOver(){
        new BukkitRunnable() {
            @Override
            public void run() {
//                setPlayerScoreboard((Player) Bukkit.getOnlinePlayers());
                setTitle("&b方块竞速");
                setSlot(15, ChatColor.YELLOW + "游戏结束");
                setSlot(14, ChatColor.YELLOW + "当前模式：" + (GameManager.extremeMode ? "极限竞速模式" : "普通模式"));
                setSlot(13, ChatColor.YELLOW + "本局游戏胜利队伍：" + GameManager.winnerTeamDisplayname);
                cancel();
            }
        }.runTaskLater(Main.getInstance(),20L);


    }
    private static String setDifficultyDisplay(String block) {
        try {
            if (easyBlock.contains(block)) {
                return ChatColor.GREEN + "简单 " + "| " + TranslationManager.getValue(block);
            } else if (normalBlock.contains(block)) {
                return ChatColor.YELLOW + "中等 " + "| " + TranslationManager.getValue(block);
            } else if (hardBlock.contains(block)) {
                return ChatColor.RED + "困难 " + "| " + TranslationManager.getValue(block);
            } else if (dyedBlock.contains(block)) {
                return ChatColor.LIGHT_PURPLE + "染色 " + "| " + TranslationManager.getValue(block);
            } else if (endBlock.contains(block)) {
                return ChatColor.YELLOW + "末地 " + "| " + TranslationManager.getValue(block);
            }
        } catch (Exception e) {
            Bukkit.getLogger().info(e.toString());
        }
        return null;
    }
    public static void update() {
//        if (!GameManager.gameStatus) setPreScoreboard();
//        else setGameScoreboard();

//        if (GameManager.gameStatus){
//            setGameScoreboard();
//        } else if (GameTick.redCompleteAmount != 0 || GameTick.blueCompleteAmount != 0) {
//            setGameOver();
//        } else {
//            setPreScoreboard();
//        }

        if (!GameManager.gameStatus){
            if (GameManager.gameOver) {
                setGameOver();
            } else {
                setPreScoreboard();
            }
        } else {
            setGameScoreboard();
        }
    }
    /**
     * https://github.com/Andy-K-Sparklight/PluginDiaryCode/blob/master/RarityCommons/src/main/java/rarityeg/commons/ScoreHelper.java
     * Help build up a scoreboard.
     * Considering RarityCommons isn't designed for Paper only,
     * we won't make migrations before Bukkit and Spigot support Kyori Powered Adventure.
     *
     * @author crisdev333
     * @author RarityEG
     * @see net.kyori.adventure.text.Component
     */
    private static String genEntry(int slot) {
        return ChatColor.values()[slot].toString();
    }

    private static void setTitle(String title) {
        title = ChatColor.translateAlternateColorCodes('&', title);
        sidebar.setDisplayName(title.length() > 32 ? title.substring(0, 32) : title);
    }

    private static void setSlot(int slot, String text) {
        Team team = scoreboard.getTeam("SLOT_" + slot);
        String entry = genEntry(slot);
        if (!scoreboard.getEntries().contains(entry)) {
            sidebar.getScore(entry).setScore(slot);
        }

        text = Color.format(text);
        String pre = getFirstSplit(text);
        String suf = getFirstSplit(ChatColor.getLastColors(pre) + getSecondSplit(text));
        if (team == null) {
            return;
        }
        team.setPrefix(pre);
        team.setSuffix(suf);
    }

    private static String getFirstSplit(String s) {
        return s.length() > 16 ? s.substring(0, 16) : s;
    }

    private static String getSecondSplit(String s) {
        if (s.length() > 32) {
            s = s.substring(0, 32);
        }
        return s.length() > 16 ? s.substring(16) : "";
    }
}
