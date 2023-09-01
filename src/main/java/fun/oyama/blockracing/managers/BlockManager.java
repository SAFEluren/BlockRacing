package fun.oyama.blockracing.managers;

import fun.oyama.blockracing.listeners.playerClickEvent;

import java.util.ArrayList;
import java.util.Collections;

public class BlockManager {
    // 设置方块库
    public static boolean blockAmountCheckout = false;
    public static int maxBlockAmount;
    public static String[] easyBlocks;
    public static String[] normalBlocks;
    public static String[] hardBlocks;
    public static String[] dyedBlocks;
    public static String[] endBlocks;
    public static String[] blocks;

    public static void init() {
        ArrayList<String> var = new ArrayList<>();
        Collections.addAll(var, easyBlocks);
        if (playerClickEvent.enableNormalBlock) Collections.addAll(var, normalBlocks);
        if (playerClickEvent.enableHardBlock) Collections.addAll(var, hardBlocks);
        if (playerClickEvent.enableDyedBlock) Collections.addAll(var, dyedBlocks);
        if (playerClickEvent.enableEndBlock) Collections.addAll(var, endBlocks);
        if (playerClickEvent.blockAmount > var.size()) {
            blockAmountCheckout = false;
            maxBlockAmount = var.size();
            return;
        } else {
            blockAmountCheckout = true;
        }
        blocks = var.toArray(new String[0]);
    }
}
