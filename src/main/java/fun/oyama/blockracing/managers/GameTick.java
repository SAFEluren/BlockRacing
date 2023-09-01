package fun.oyama.blockracing.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import fun.oyama.blockracing.utils.ItemBuilder;

import static fun.oyama.blockracing.utils.CountdownRestart.countdownRestart;

public class GameTick extends BukkitRunnable {
    public static int redCompleteAmount;
    public static int blueCompleteAmount;

    // 每5t执行一次 在GameManager注册
    @Override
    public void run() {
        try {
            // 检查物品栏
            checkRedInventory();
            checkBlueInventory();
        } catch (Exception e) {
            Bukkit.getLogger().info(e.toString());
        }

        if (checkWinner()){
            cancel();
        }
    }

    // 胜利检测
    private boolean checkWinner() {
        if (GameManager.blueCurrentBlocks.isEmpty()) {
            GameManager.setWinner("blue",false);
            countdownRestart(30);
            return true;
        }

        if (GameManager.redCurrentBlocks.isEmpty()) {
            GameManager.setWinner("red",false);
            countdownRestart(30);
            return true;
        }
        return false;
    }
    // 检查红队物品栏
    private void checkRedInventory() throws Exception {
        for (Player player : GameManager.redTeamPlayer) {
            for (String block : GameManager.redCurrentBlocks) {
                if (player.getInventory().contains(Material.valueOf(block))) {
                    redTaskComplete(block);
                    return;
                }
            }
        }
        for (String block : GameManager.redCurrentBlocks) {
            if (InventoryManager.redTeamChest1.contains(Material.valueOf(block)) || InventoryManager.redTeamChest2.contains(Material.valueOf(block)) || InventoryManager.redTeamChest3.contains(Material.valueOf(block))) {
                redTaskComplete(block);
                return;
            }
        }
    }

    // 检查蓝队物品栏
    private void checkBlueInventory() throws Exception {
        for (Player player : GameManager.blueTeamPlayer) {
            for (String block : GameManager.blueCurrentBlocks) {
                if (player.getInventory().contains(Material.valueOf(block))) {
                    blueTaskComplete(block);
                    return;
                }
            }
        }
        for (String block : GameManager.blueCurrentBlocks) {
            if (InventoryManager.blueTeamChest1.contains(Material.valueOf(block)) || InventoryManager.blueTeamChest2.contains(Material.valueOf(block)) || InventoryManager.blueTeamChest3.contains(Material.valueOf(block))) {
                blueTaskComplete(block);
                return;
            }
        }
    }

    // 红队获得目标方块
    private void redTaskComplete(String block) throws Exception {
        Bukkit.broadcastMessage(ChatColor.RED + "红队收集了" + TranslationManager.getValue(block));
//        ConsoleCommandHandler.send("tellraw @a {\"text\": \"\\u00a7c红队\\u00a7a收集了\u00a7b" + TranslationManager.getValue(block) + "\"}");
//        Bukkit.getLogger().info("红队收集了" + TranslationManager.getValue(block));
//        ConsoleCommandHandler.send("execute as @a at @s run playsound minecraft:entity.experience_orb.pickup player @s");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        }
        redCompleteAmount += 1;
        GameManager.redCurrentBlocks.remove(block);
        if (GameManager.redTeamBlocks.size() >= 1) {
            GameManager.redCurrentBlocks.add(GameManager.redTeamBlocks.get(0));
            GameManager.redTeamBlocks.remove(0);
        }
        ScoreboardManager.redTeamScore += 1;
        ScoreboardManager.update();
        if (!GameManager.extremeMode) {
            // 将一组该物品放到蓝队队伍箱子
            if (InventoryManager.blueTeamChest1.firstEmpty() != -1) {
                ItemStack stack = new ItemStack(Material.valueOf(block));
                ItemBuilder TeamChestBuilder = new ItemBuilder(stack);
                TeamChestBuilder.setAmount(1);
                TeamChestBuilder.toItemStack();
                InventoryManager.blueTeamChest1.setItem(InventoryManager.blueTeamChest1.firstEmpty(), stack);
            } else if (InventoryManager.blueTeamChest2.firstEmpty() != -1) {
                ItemStack stack = new ItemStack(Material.valueOf(block));
                ItemBuilder TeamChestBuilder = new ItemBuilder(stack);
                TeamChestBuilder.setAmount(1);
                TeamChestBuilder.toItemStack();
                InventoryManager.blueTeamChest2.setItem(InventoryManager.blueTeamChest2.firstEmpty(), stack);
            } else if (InventoryManager.blueTeamChest3.firstEmpty() != -1) {
                ItemStack stack = new ItemStack(Material.valueOf(block));
                ItemBuilder TeamChestBuilder = new ItemBuilder(stack);
                TeamChestBuilder.setAmount(1);
                TeamChestBuilder.toItemStack();
                InventoryManager.blueTeamChest3.setItem(InventoryManager.blueTeamChest3.firstEmpty(), stack);
            } else {
//                ConsoleCommandHandler.send("tellraw @a \"\\u00a74蓝队队伍箱子已满！" + TranslationManager.getValue(block) + "无法放入队伍箱子！\"");
                Bukkit.broadcastMessage(ChatColor.DARK_RED + "蓝队队伍箱子已满,无法将以下物品放进队伍箱子:" + TranslationManager.getValue(block));
            }
        }
    }

    // 蓝队获得目标方块
    private void blueTaskComplete(String block) throws Exception {
        Bukkit.broadcastMessage(ChatColor.BLUE + "蓝队收集了" + TranslationManager.getValue(block));
//        Bukkit.getLogger().info("蓝队收集了" + TranslationManager.getValue(block));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        }

        blueCompleteAmount += 1;
        GameManager.blueCurrentBlocks.remove(block);
        if (GameManager.blueTeamBlocks.size() >= 1) {
            GameManager.blueCurrentBlocks.add(GameManager.blueTeamBlocks.get(0));
            GameManager.blueTeamBlocks.remove(0);
        }
        ScoreboardManager.blueTeamScore += 1;
        ScoreboardManager.update();
        if (!GameManager.extremeMode) {
            // 将一组该物品放到红队队伍箱子
            if (InventoryManager.redTeamChest1.firstEmpty() != -1) {
                ItemStack stack = new ItemStack(Material.valueOf(block));
                ItemBuilder TeamChestBuilder = new ItemBuilder(stack);
                TeamChestBuilder.setAmount(1);
                TeamChestBuilder.toItemStack();
                InventoryManager.redTeamChest1.setItem(InventoryManager.redTeamChest1.firstEmpty(), stack);
            } else if (InventoryManager.redTeamChest2.firstEmpty() != -1) {
                ItemStack stack = new ItemStack(Material.valueOf(block));
                ItemBuilder TeamChestBuilder = new ItemBuilder(stack);
                TeamChestBuilder.setAmount(1);
                TeamChestBuilder.toItemStack();
                InventoryManager.redTeamChest2.setItem(InventoryManager.redTeamChest2.firstEmpty(), stack);
            } else if (InventoryManager.redTeamChest3.firstEmpty() != -1) {
                ItemStack stack = new ItemStack(Material.valueOf(block));
                ItemBuilder TeamChestBuilder = new ItemBuilder(stack);
                TeamChestBuilder.setAmount(1);
                TeamChestBuilder.toItemStack();
                InventoryManager.redTeamChest3.setItem(InventoryManager.redTeamChest3.firstEmpty(), stack);
            } else {
                Bukkit.broadcastMessage(ChatColor.DARK_RED + "红队队伍箱子已满,无法将以下物品放进队伍箱子:" + TranslationManager.getValue(block));

            }
        }
    }
}

