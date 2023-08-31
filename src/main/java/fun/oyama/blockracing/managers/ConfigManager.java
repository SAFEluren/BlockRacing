package fun.oyama.blockracing.managers;

import org.bukkit.configuration.file.FileConfiguration;
import fun.oyama.blockracing.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    static FileConfiguration config;

    // 读取配置文件
    public ConfigManager() throws IOException {
        BlockManager.easyBlocks = readFile("EasyBlocks.txt");
        BlockManager.normalBlocks = readFile("NormalBlocks.txt");
        BlockManager.hardBlocks = readFile("HardBlocks.txt");
        BlockManager.dyedBlocks = readFile("DyedBlocks.txt");
        BlockManager.endBlocks = readFile("EndBlocks.txt");
    }

    public static String[] readFile(String fileName) throws IOException {
        File file = new File(Main.getInstance().getDataFolder(), fileName);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.equals("")) lines.add(line);
        }
        reader.close();
        return lines.toArray(new String[0]);
    }

    public static FileConfiguration getConfig() {
        return config;
    }
}
