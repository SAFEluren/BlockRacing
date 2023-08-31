package fun.oyama.blockracing.listeners;

import fun.oyama.blockracing.Main;
import fun.oyama.blockracing.managers.BlockManager;
import fun.oyama.blockracing.managers.GameManager;
import fun.oyama.blockracing.managers.InventoryManager;
import fun.oyama.blockracing.managers.ScoreboardManager;
import fun.oyama.blockracing.utils.ConsoleCommandHandler;
import fun.oyama.blockracing.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static fun.oyama.blockracing.managers.GameManager.gameStatus;
import static fun.oyama.blockracing.managers.InventoryManager.menu;
import static fun.oyama.blockracing.managers.InventoryManager.settings;
import static org.bukkit.Bukkit.broadcast;
import static org.bukkit.Bukkit.broadcastMessage;


public class EventListener implements Listener {
    public static ArrayList<Player> locateCommandPermission = new ArrayList<>();
    public static boolean enableNormalBlock = false;
    public static boolean enableHardBlock = false;
    public static boolean enableDyedBlock = false;
    public static boolean enableEndBlock = false;
    public static ArrayList<String> redTeamPlayerString = new ArrayList<>();
    public static ArrayList<String> blueTeamPlayerString = new ArrayList<>();
    public static int blockAmount = 50;
    public static List<Player> prepareList = new ArrayList<>();
    public static HashMap<String, Location> point1 = new HashMap<>();
    public static HashMap<String, Location> point2 = new HashMap<>();
    public static HashMap<String, Location> point3 = new HashMap<>();
    public static HashMap<Player, Boolean> randomTP = new HashMap<>();
    public static boolean canStart = false;
    public static ArrayList<Player> editAmountPlayer = new ArrayList<>();
    Random r = new Random();
    boolean flag = false;
    private boolean redIsRolled = false;
    private boolean blueIsRolled = false;

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
    public void onPlayerQuit(PlayerQuitEvent e) {
        // 在线列表移除玩家
        GameManager.inGamePlayer.remove(e.getPlayer());
        prepareList.remove(e.getPlayer());
        editAmountPlayer.remove(e.getPlayer());

    }

    @EventHandler
    public void onPlayerSwap(PlayerSwapHandItemsEvent e) {
        if (e.getPlayer().isSneaking()) {
            if (!gameStatus) e.getPlayer().openInventory(settings);
            else e.getPlayer().openInventory(menu);
            e.setCancelled(true);
        }
    }

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
                blockAmount = Integer.parseInt(e.getMessage());
                flag = true;
            } catch (Exception ex) {
                e.getPlayer().sendMessage(ChatColor.RED + "请输入正确数字！如果您想退出输入模式，请发送quit");
                flag = false;
            } finally {
                e.setCancelled(true);
            }
            if (flag) {
                if (blockAmount < 10) {
//                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> ConsoleCommandHandler.send("tellraw @a {\"color\": \"green\",\"text\": \"需要收集的方块数量更改为10\"}"), 1L);
                    e.getPlayer().sendMessage(ChatColor.GREEN+ "目标方块数量最小为10，自动修改为10");
                    blockAmount = 10;
                } else {
//                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> ConsoleCommandHandler.send("tellraw @a {\"color\": \"green\",\"text\": \"将需要收集的方块数量更改为" + blockAmount + "\"}"), 1L);
                    Bukkit.broadcastMessage(ChatColor.GREEN+ "更改方块数量为 " + blockAmount);

                }
                editAmountPlayer.remove(e.getPlayer());
                ScoreboardManager.update();
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        e.getPlayer().sendMessage(ChatColor.AQUA + "提示：如果出生点附近无法放置或破坏方块，是因为服务器带有出生点保护，离开出生点附近即可！");
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        InventoryView inv = player.getOpenInventory();

        if (inv.getTitle().equals(InventoryManager.TITLE_MENU) || inv.getTitle().equals(InventoryManager.TITLE_TEAM_CHEST_SWITCH) || inv.getTitle().equals(InventoryManager.TITLE_WAYPOINTS) || inv.getTitle().equals(InventoryManager.TITLE_SETTINGS)) {
            e.setCancelled(true);
            // 防误触处理
            if (e.getRawSlot() < 0 || e.getRawSlot() > e.getInventory().getSize()) {
                return;
            }
            ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem == null) {
                return;
            }

            // settings 设置界面
            // 难度选择
            if (clickedItem.getItemMeta().getDisplayName().equals("中等难度方块：" + ChatColor.RED + "已禁用")) {
                InventoryManager.setItem("GREEN_CONCRETE", 1,
                        "中等难度方块：" + ChatColor.GREEN + "已启用",
                        ChatColor.RED + "点击以禁用中等难度方块", 28, "settings");
                enableNormalBlock = true;
                ScoreboardManager.update();
                return;
            } else if (clickedItem.getItemMeta().getDisplayName().equals("中等难度方块：" + ChatColor.GREEN + "已启用")) {
                InventoryManager.setItem("RED_CONCRETE", 1,
                        "中等难度方块：" + ChatColor.RED + "已禁用",
                        ChatColor.GREEN + "点击以启用中等难度方块", 28, "settings");
                enableNormalBlock = false;
                ScoreboardManager.update();
                return;
            } else if (clickedItem.getItemMeta().getDisplayName().equals("困难难度方块：" + ChatColor.RED + "已禁用")) {
                InventoryManager.setItem("GREEN_CONCRETE", 1,
                        "困难难度方块：" + ChatColor.GREEN + "已启用",
                        ChatColor.RED + "点击以禁用困难难度方块", 29, "settings");
                enableHardBlock = true;
                ScoreboardManager.update();
                return;
            } else if (clickedItem.getItemMeta().getDisplayName().equals("困难难度方块：" + ChatColor.GREEN + "已启用")) {
                InventoryManager.setItem("RED_CONCRETE", 1,
                        "困难难度方块：" + ChatColor.RED + "已禁用",
                        ChatColor.GREEN + "点击以启用困难难度方块", 29, "settings");
                enableHardBlock = false;
                ScoreboardManager.update();
                return;
            } else if (clickedItem.getItemMeta().getDisplayName().equals("染色方块：" + ChatColor.RED + "已禁用")) {
                InventoryManager.setItem("GREEN_CONCRETE", 1,
                        "染色方块：" + ChatColor.GREEN + "已启用",
                        ChatColor.RED + "点击以禁用染色方块", 30, "settings");
                enableDyedBlock = true;
                ScoreboardManager.update();
                return;
            } else if (clickedItem.getItemMeta().getDisplayName().equals("染色方块：" + ChatColor.GREEN + "已启用")) {
                InventoryManager.setItem("RED_CONCRETE", 1,
                        "染色方块：" + ChatColor.RED + "已禁用",
                        ChatColor.GREEN + "点击以启用染色方块", 30, "settings");
                enableDyedBlock = false;
                ScoreboardManager.update();
                return;
            } else if (clickedItem.getItemMeta().getDisplayName().equals("末地方块：" + ChatColor.RED + "已禁用")) {
                InventoryManager.setItem("GREEN_CONCRETE", 1,
                        "末地方块：" + ChatColor.GREEN + "已启用",
                        ChatColor.RED + "点击以禁用染色方块", 31, "settings");
                enableEndBlock = true;
                ScoreboardManager.update();
                return;
            } else if (clickedItem.getItemMeta().getDisplayName().equals("末地方块：" + ChatColor.GREEN + "已启用")) {
                InventoryManager.setItem("RED_CONCRETE", 1,
                        "末地方块：" + ChatColor.RED + "已禁用",
                        ChatColor.GREEN + "点击以启用染色方块", 31, "settings");
                enableEndBlock = false;
                ScoreboardManager.update();
                return;
            }

            // 队伍选择
            if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "加入红队")) {
                ScoreboardManager.red.addEntry(player.getName());
//                ConsoleCommandHandler.send("tellraw @a {\"text\": \"\\u00a7c" + player.getName() + "加入了红队！\"}");
                broadcastMessage(ChatColor.GREEN + player.getDisplayName() + "加入了红队!");
                GameManager.redTeamPlayer.add(player);
                redTeamPlayerString.add(player.getName());
                if (GameManager.blueTeamPlayer.contains(player)) {
                    GameManager.blueTeamPlayer.remove(player);
                    blueTeamPlayerString.remove(player.getName());
                    ScoreboardManager.blue.removeEntry(player.getName());
                }
                return;
            } else if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "加入蓝队")) {
                ScoreboardManager.blue.addEntry(player.getName());
//                ConsoleCommandHandler.send("tellraw @a {\"text\": \"\\u00a79" + player.getName() + "加入了蓝队！\"}");
                broadcastMessage(ChatColor.GREEN + player.getDisplayName() + "加入了蓝队!");
                GameManager.blueTeamPlayer.add(player);
                blueTeamPlayerString.add(player.getName());
                if (GameManager.redTeamPlayer.contains(player)) {
                    GameManager.redTeamPlayer.remove(player);
                    redTeamPlayerString.remove(player.getName());
                    ScoreboardManager.red.removeEntry(player.getName());
                }
                return;
            }

            // 准备
            if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "准备")) {
                if (!prepareList.contains(player)) {
                    prepareList.add(player);
                    Bukkit.broadcastMessage(ChatColor.AQUA + player.getDisplayName() + "已准备！");
                    if (prepareList.size() > 1 && prepareList.size() == Bukkit.getOnlinePlayers().size()) {
                        canStart = true;
                        Bukkit.broadcastMessage(ChatColor.AQUA + "所有玩家已准备");
                    }
                    return;
                } else {
                    player.sendMessage("§c您已经准备过了！");
//                    ConsoleCommandHandler.send("tellraw @a \"§c您已经准备过了！\"");
                    return;
                }
            }

            // 开始游戏
            ArrayList<Player> unPreparePlayer = new ArrayList<>();
            ArrayList<String> unPrepareList = new ArrayList<>();
            if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "开始游戏")) {
                if (!canStart) {
                    // 人数检测
                    if (Bukkit.getOnlinePlayers().size() == 1) {
                        broadcastMessage(ChatColor.DARK_RED + "游戏无法开始！至少需要两个人！");
                        return;
                    }
                    // 存在未准备玩家
                    unPreparePlayer.clear();
                    unPrepareList.clear();
                    unPreparePlayer.addAll(Bukkit.getOnlinePlayers());
                    for (Player p : prepareList) unPreparePlayer.remove(p);
                    for (Player p : unPreparePlayer) unPrepareList.add(p.getName());
                    broadcastMessage(ChatColor.DARK_RED + "游戏无法开始！还有玩家未准备！");
                    broadcastMessage(ChatColor.DARK_RED + "未准备的玩家：" + ChatColor.YELLOW + unPrepareList);
                    return;
                } else {
                    // 队伍人数检测
                    if (GameManager.redTeamPlayer.size() == 0 || GameManager.blueTeamPlayer.size() == 0) {
                        broadcastMessage(ChatColor.DARK_RED + "游戏无法开始！有队伍空无一人！");
                        return;
                    }
                    // 目标方块数量检测
                    BlockManager.init();
                    if (!BlockManager.blockAmountCheckout) {
                        broadcastMessage(ChatColor.RED + "目标方块数量过多！最多只能设置为" + BlockManager.maxBlockAmount);
                        return;
                    }
//                    ConsoleCommandHandler.send("tellraw @a \"§b游戏开始！\"");
                    Bukkit.broadcastMessage(ChatColor.GREEN + "游戏开始!");
                    player.closeInventory();
                    GameManager.gameStart();
                }
            }

            // 更改方块数量
            if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "目标方块数量")) {
                player.closeInventory();
                editAmountPlayer.add(player);
                player.sendMessage(ChatColor.YELLOW + "请直接发送目标方块数量，发送quit取消输入");
                return;
            }

            // 切换模式
            if (clickedItem.getItemMeta().getDisplayName().equals("点击此处切换至" + ChatColor.YELLOW + "极限竞速模式")) {
                GameManager.extremeMode = true;
                ItemStack stack = new ItemStack(Objects.requireNonNull(Material.GREEN_CONCRETE));
                ItemBuilder builder = new ItemBuilder(stack);
                builder.setAmount(1);
                builder.setDisplayName("点击此处切换至" + ChatColor.GREEN + "普通模式");
                builder.setLore(ChatColor.GREEN + "普通模式：", ChatColor.GREEN + "两个队伍需要收集的方块及顺序完全随机，", ChatColor.GREEN + "收集方块会将一组该方块给到对方的队伍箱子里！", ChatColor.YELLOW + "极限竞速模式：", ChatColor.YELLOW + "两个队伍需要收集的方块及顺序完全相同，", ChatColor.YELLOW + "收集方块将不会给予一组方块到对方队伍！");
                builder.toItemStack();
                settings.setItem(34, stack);
                ScoreboardManager.update();
            } else if (clickedItem.getItemMeta().getDisplayName().equals("点击此处切换至" + ChatColor.GREEN + "普通模式")) {
                GameManager.extremeMode = false;
                ItemStack stack = new ItemStack(Objects.requireNonNull(Material.RED_CONCRETE));
                ItemBuilder builder = new ItemBuilder(stack);
                builder.setAmount(1);
                builder.setDisplayName("点击此处切换至" + ChatColor.YELLOW + "极限竞速模式");
                builder.setLore(ChatColor.GREEN + "普通模式：", ChatColor.GREEN + "两个队伍需要收集的方块及顺序完全随机，", ChatColor.GREEN + "收集方块会将一组该方块给到对方的队伍箱子里！", ChatColor.YELLOW + "极限竞速模式：", ChatColor.YELLOW + "两个队伍需要收集的方块及顺序完全相同，", ChatColor.YELLOW + "收集方块将不会给予一组方块到对方队伍！");
                builder.toItemStack();
                settings.setItem(34, stack);
                ScoreboardManager.update();
            }

            // menu 菜单界面
            if (clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.TEAM_CHEST)) {
                Objects.requireNonNull(Bukkit.getPlayer(e.getWhoClicked().getName())).openInventory(InventoryManager.chestSwitch);
                return;
            }
            if (clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.ROLL)) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked()) & !redIsRolled) {
                    for (int i = 0; i < 4; i++) {
                        if (GameManager.redCurrentBlocks.size() != 0) {
                            GameManager.redCurrentBlocks.remove(0);
                            GameManager.redCurrentBlocks.add(BlockManager.easyBlocks[r.nextInt(BlockManager.easyBlocks.length)]);
                        } else break;
                    }

//                    ConsoleCommandHandler.send("tellraw @a \"\\u00a7c红队刷新了所需方块！\"");
                    broadcastMessage(ChatColor.GREEN+ "红队刷新了所需方块！");
                    redIsRolled = true;
                    ScoreboardManager.redTeamScore = 0;
                    ScoreboardManager.update();
                } else if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked()) & redIsRolled) {
                    e.getWhoClicked().sendMessage(ChatColor.DARK_RED + "您的队伍已经使用过Roll了！");
                }
                if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked()) & !blueIsRolled) {
                    for (int i = 0; i < 4; i++) {
                        if (!GameManager.blueCurrentBlocks.isEmpty()) {
                            GameManager.blueCurrentBlocks.remove(0);
                            GameManager.blueCurrentBlocks.add(BlockManager.easyBlocks[r.nextInt(BlockManager.easyBlocks.length)]);
                        } else break;
                    }
//                    ConsoleCommandHandler.send("tellraw @a \"\\u00a79蓝队Roll掉了所需方块！\"");
                    broadcastMessage(ChatColor.GREEN+ "蓝队刷新了所需方块！");
                    blueIsRolled = true;
                    ScoreboardManager.blueTeamScore = 0;
                    ScoreboardManager.update();
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked()) & blueIsRolled) {
                    e.getWhoClicked().sendMessage(ChatColor.DARK_RED + "您的队伍已经使用过Roll了！");
                }
                return;
            }

            if (clickedItem.getItemMeta().displayName().equals(InventoryManager.WAYPOINTS)) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    Objects.requireNonNull(Bukkit.getPlayer(e.getWhoClicked().getName())).openInventory(InventoryManager.redWayPoints);
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    Objects.requireNonNull(Bukkit.getPlayer(e.getWhoClicked().getName())).openInventory(InventoryManager.blueWayPoints);
                }
                return;
            }

            if (clickedItem.getItemMeta().displayName().equals(InventoryManager.LOCATE)) {
                if (locateCommandPermission.contains((Player) e.getWhoClicked())) {
                    e.getWhoClicked().sendMessage(ChatColor.DARK_RED + "您还没有使用locate命令！无法再次购买！");
                    return;
                }
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    if (ScoreboardManager.redTeamScore >= GameManager.locateCost) {
                        ScoreboardManager.redTeamScore -= GameManager.locateCost;
                        ScoreboardManager.update();
                        locateCommandPermission.add((Player) e.getWhoClicked());
                        ConsoleCommandHandler.send("tellraw @a \"\\u00a7a" + e.getWhoClicked().getName() + "购买了定位命令使用权限！\"");
                    } else {
                        e.getWhoClicked().sendMessage(ChatColor.DARK_RED + "积分不足！");
                    }
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    if (ScoreboardManager.blueTeamScore >= GameManager.locateCost) {
                        ScoreboardManager.blueTeamScore -= GameManager.locateCost;
                        ScoreboardManager.update();
                        locateCommandPermission.add((Player) e.getWhoClicked());
                        ConsoleCommandHandler.send("tellraw @a \"\\u00a7a" + e.getWhoClicked().getName() + "购买了定位命令使用权限！\"");
                    } else {
                        e.getWhoClicked().sendMessage(ChatColor.DARK_RED + "积分不足！");
                    }
                }
            }

            if (clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.RANDOMTP)) {
                randomTP.putIfAbsent(player, false);
                if (randomTP.get(player)) {
                    if (GameManager.redTeamPlayer.contains(player)) {
                        if (ScoreboardManager.redTeamScore < 2) {
                            player.sendMessage(ChatColor.DARK_RED + "积分不足！");
                            return;
                        }
                    } else {
                        if (ScoreboardManager.blueTeamScore < 2) {
                            player.sendMessage(ChatColor.DARK_RED + "积分不足！");
                            return;
                        }
                    }
                }
                Player p = (Player) e.getWhoClicked();
                e.getWhoClicked().closeInventory();
                World playerWorld = Bukkit.getWorld("world");
                double randX = r.nextInt(20000) - 10000;
                double randZ = r.nextInt(20000) - 10000;
                Location offset = new Location(playerWorld, randX, 0, randZ).toHighestLocation();
                double Y = offset.getY() + 1;
                offset.setY(Y);
                p.teleport(offset);
//                ConsoleCommandHandler.send("tellraw @a \"\\u00a7a玩家" + player.getName() + "使用了随机传送！\"");
                p.sendMessage(ChatColor.GREEN + "已传送到 " + offset.getX() + " " + offset.getY() + " " + offset.getZ());
                if (randomTP.get(player)) {
                    if (GameManager.redTeamPlayer.contains(player)) ScoreboardManager.redTeamScore -= 2;
                    else ScoreboardManager.blueTeamScore -= 2;
                } else randomTP.put(player, true);
                ScoreboardManager.update();
            }

            // chestSwitch 箱子选择界面
            if (clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.TEAM_CHEST1)) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    Objects.requireNonNull(Bukkit.getPlayer(e.getWhoClicked().getName())).openInventory(InventoryManager.redTeamChest1);
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    Objects.requireNonNull(Bukkit.getPlayer(e.getWhoClicked().getName())).openInventory(InventoryManager.blueTeamChest1);
                }
                return;
            }
            if (clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.TEAM_CHEST2)) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    Objects.requireNonNull(Bukkit.getPlayer(e.getWhoClicked().getName())).openInventory(InventoryManager.redTeamChest2);
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    Objects.requireNonNull(Bukkit.getPlayer(e.getWhoClicked().getName())).openInventory(InventoryManager.blueTeamChest2);
                }
                return;
            }
            if (clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.TEAM_CHEST3)) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    Objects.requireNonNull(Bukkit.getPlayer(e.getWhoClicked().getName())).openInventory(InventoryManager.redTeamChest3);
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    Objects.requireNonNull(Bukkit.getPlayer(e.getWhoClicked().getName())).openInventory(InventoryManager.blueTeamChest3);
                }
                return;
            }

            // wayPoint 记录点界面
            if (clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.WAYPOINTS1)) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    ItemStack Waypoints1 = new ItemStack(Material.FILLED_MAP);
                    ItemBuilder Waypoints1Builder = new ItemBuilder(Waypoints1);
                    Waypoints1Builder.setAmount(1);
                    Waypoints1Builder.setDisplayName(InventoryManager.ACTIVATED_WAYPOINTS1);
                    Waypoints1Builder.setLore(ChatColor.GREEN + "维度：" + e.getWhoClicked().getWorld().getName(), ChatColor.GREEN + "坐标：" + e.getWhoClicked().getLocation().getBlockX() + ", " + e.getWhoClicked().getLocation().getBlockY() + ", " + e.getWhoClicked().getLocation().getBlockZ(), ChatColor.AQUA + "左键点击传送，右键点击删除");
                    Waypoints1Builder.toItemStack();
                    InventoryManager.redWayPoints.setItem(0, Waypoints1);
                    point1.put("red", e.getWhoClicked().getLocation());
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    ItemStack Waypoints1 = new ItemStack(Material.FILLED_MAP);
                    ItemBuilder Waypoints1Builder = new ItemBuilder(Waypoints1);
                    Waypoints1Builder.setAmount(1);
                    Waypoints1Builder.setDisplayName(InventoryManager.ACTIVATED_WAYPOINTS1);
                    Waypoints1Builder.setLore(ChatColor.GREEN + "维度：" + e.getWhoClicked().getWorld().getName(), ChatColor.GREEN + "坐标：" + e.getWhoClicked().getLocation().getBlockX() + ", " + e.getWhoClicked().getLocation().getBlockY() + ", " + e.getWhoClicked().getLocation().getBlockZ(), ChatColor.AQUA + "左键点击传送，右键点击删除");
                    Waypoints1Builder.toItemStack();
                    InventoryManager.blueWayPoints.setItem(0, Waypoints1);
                    point1.put("blue", e.getWhoClicked().getLocation());
                }
            } else if (clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.WAYPOINTS2)) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    ItemStack Waypoints2 = new ItemStack(Material.FILLED_MAP);
                    ItemBuilder Waypoints2Builder = new ItemBuilder(Waypoints2);
                    Waypoints2Builder.setAmount(1);
                    Waypoints2Builder.setDisplayName(InventoryManager.ACTIVATED_WAYPOINTS2);
                    Waypoints2Builder.setLore(ChatColor.GREEN + "维度：" + e.getWhoClicked().getWorld().getName(), ChatColor.GREEN + "坐标：" + e.getWhoClicked().getLocation().getBlockX() + ", " + e.getWhoClicked().getLocation().getBlockY() + ", " + e.getWhoClicked().getLocation().getBlockZ(), ChatColor.AQUA + "左键点击传送，右键点击删除");
                    Waypoints2Builder.toItemStack();
                    InventoryManager.redWayPoints.setItem(1, Waypoints2);
                    point2.put("red", e.getWhoClicked().getLocation());
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    ItemStack Waypoints2 = new ItemStack(Material.FILLED_MAP);
                    ItemBuilder Waypoints2Builder = new ItemBuilder(Waypoints2);
                    Waypoints2Builder.setAmount(1);
                    Waypoints2Builder.setDisplayName(InventoryManager.ACTIVATED_WAYPOINTS2);
                    Waypoints2Builder.setLore(ChatColor.GREEN + "维度：" + e.getWhoClicked().getWorld().getName(), ChatColor.GREEN + "坐标：" + e.getWhoClicked().getLocation().getBlockX() + ", " + e.getWhoClicked().getLocation().getBlockY() + ", " + e.getWhoClicked().getLocation().getBlockZ(), ChatColor.AQUA + "左键点击传送，右键点击删除");
                    Waypoints2Builder.toItemStack();
                    InventoryManager.blueWayPoints.setItem(1, Waypoints2);
                    point2.put("blue", e.getWhoClicked().getLocation());
                }
            } else if (clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.WAYPOINTS3)) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    ItemStack Waypoints3 = new ItemStack(Material.FILLED_MAP);
                    ItemBuilder Waypoints3Builder = new ItemBuilder(Waypoints3);
                    Waypoints3Builder.setAmount(1);
                    Waypoints3Builder.setDisplayName(InventoryManager.ACTIVATED_WAYPOINTS3);
                    Waypoints3Builder.setLore(ChatColor.GREEN + "维度：" + e.getWhoClicked().getWorld().getName(), ChatColor.GREEN + "坐标：" + e.getWhoClicked().getLocation().getBlockX() + ", " + e.getWhoClicked().getLocation().getBlockY() + ", " + e.getWhoClicked().getLocation().getBlockZ(), ChatColor.AQUA + "左键点击传送，右键点击删除");
                    Waypoints3Builder.toItemStack();
                    InventoryManager.redWayPoints.setItem(2, Waypoints3);
                    point3.put("red", e.getWhoClicked().getLocation());
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    ItemStack Waypoints3 = new ItemStack(Material.FILLED_MAP);
                    ItemBuilder Waypoints3Builder = new ItemBuilder(Waypoints3);
                    Waypoints3Builder.setAmount(1);
                    Waypoints3Builder.setDisplayName(InventoryManager.ACTIVATED_WAYPOINTS3);
                    Waypoints3Builder.setLore(ChatColor.GREEN + "维度：" + e.getWhoClicked().getWorld().getName(), ChatColor.GREEN + "坐标：" + e.getWhoClicked().getLocation().getBlockX() + ", " + e.getWhoClicked().getLocation().getBlockY() + ", " + e.getWhoClicked().getLocation().getBlockZ(), ChatColor.AQUA + "左键点击传送，右键点击删除");
                    Waypoints3Builder.toItemStack();
                    InventoryManager.blueWayPoints.setItem(2, Waypoints3);
                    point3.put("blue", e.getWhoClicked().getLocation());
                }
            } else if ((clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.ACTIVATED_WAYPOINTS1)) & e.isLeftClick()) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    (e.getWhoClicked()).teleport(point1.get("red"));
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    (e.getWhoClicked()).teleport(point1.get("blue"));
                }
            } else if ((clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.ACTIVATED_WAYPOINTS2)) & e.isLeftClick()) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    (e.getWhoClicked()).teleport(point2.get("red"));
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    (e.getWhoClicked()).teleport(point2.get("blue"));
                }
            } else if ((clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.ACTIVATED_WAYPOINTS3)) & e.isLeftClick()) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    (e.getWhoClicked()).teleport(point3.get("red"));
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    (e.getWhoClicked()).teleport(point3.get("blue"));
                }
            } else if ((clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.ACTIVATED_WAYPOINTS1)) & e.isRightClick()) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    ItemStack Waypoints1 = new ItemStack(Material.MAP);
                    ItemBuilder Waypoints1Builder = new ItemBuilder(Waypoints1);
                    Waypoints1Builder.setAmount(1);
                    Waypoints1Builder.setDisplayName(InventoryManager.WAYPOINTS1);
                    Waypoints1Builder.setLore(ChatColor.AQUA + "左键点击创建记录点");
                    Waypoints1Builder.toItemStack();
                    InventoryManager.redWayPoints.setItem(0, Waypoints1);
                    point1.remove("red");
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    ItemStack bWayPoints1 = new ItemStack(Material.MAP);
                    ItemBuilder bWayPoints1Builder = new ItemBuilder(bWayPoints1);
                    bWayPoints1Builder.setAmount(1);
                    bWayPoints1Builder.setDisplayName(InventoryManager.WAYPOINTS1);
                    bWayPoints1Builder.setLore(ChatColor.AQUA + "左键点击创建记录点");
                    bWayPoints1Builder.toItemStack();
                    InventoryManager.blueWayPoints.setItem(0, bWayPoints1);
                    point1.remove("blue");
                }
            } else if ((clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.ACTIVATED_WAYPOINTS2)) & e.isRightClick()) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    ItemStack Waypoints2 = new ItemStack(Material.MAP);
                    ItemBuilder Waypoints2Builder = new ItemBuilder(Waypoints2);
                    Waypoints2Builder.setAmount(1);
                    Waypoints2Builder.setDisplayName(InventoryManager.WAYPOINTS2);
                    Waypoints2Builder.setLore(ChatColor.AQUA + "左键点击创建记录点");
                    Waypoints2Builder.toItemStack();
                    InventoryManager.redWayPoints.setItem(1, Waypoints2);
                    point2.remove("red");
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    ItemStack bWayPoints2 = new ItemStack(Material.MAP);
                    ItemBuilder bWayPoints2Builder = new ItemBuilder(bWayPoints2);
                    bWayPoints2Builder.setAmount(1);
                    bWayPoints2Builder.setDisplayName(InventoryManager.WAYPOINTS2);
                    bWayPoints2Builder.setLore(ChatColor.AQUA + "左键点击创建记录点");
                    bWayPoints2Builder.toItemStack();
                    InventoryManager.blueWayPoints.setItem(1, bWayPoints2);
                    point2.remove("blue");
                }
            } else if ((clickedItem.getItemMeta().getDisplayName().equals(InventoryManager.ACTIVATED_WAYPOINTS3)) & e.isRightClick()) {
                if (GameManager.redTeamPlayer.contains((Player) e.getWhoClicked())) {
                    ItemStack Waypoints3 = new ItemStack(Material.MAP);
                    ItemBuilder Waypoints3Builder = new ItemBuilder(Waypoints3);
                    Waypoints3Builder.setAmount(1);
                    Waypoints3Builder.setDisplayName(InventoryManager.WAYPOINTS3);
                    Waypoints3Builder.setLore(ChatColor.AQUA + "左键点击创建记录点");
                    Waypoints3Builder.toItemStack();
                    InventoryManager.redWayPoints.setItem(2, Waypoints3);
                    point3.remove("red");
                } else if (GameManager.blueTeamPlayer.contains((Player) e.getWhoClicked())) {
                    ItemStack bWayPoints3 = new ItemStack(Material.MAP);
                    ItemBuilder bWayPoints3Builder = new ItemBuilder(bWayPoints3);
                    bWayPoints3Builder.setAmount(1);
                    bWayPoints3Builder.setDisplayName(InventoryManager.WAYPOINTS3);
                    bWayPoints3Builder.setLore(ChatColor.AQUA + "左键点击创建记录点");
                    bWayPoints3Builder.toItemStack();
                    InventoryManager.blueWayPoints.setItem(2, bWayPoints3);
                    point3.remove("blue");
                }
            }
        }
    }
}
