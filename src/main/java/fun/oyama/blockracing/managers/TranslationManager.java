package fun.oyama.blockracing.managers;

import fun.oyama.blockracing.Main;
import org.bukkit.Material;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.Objects;

public class TranslationManager {
    /**
     * 获取方块名翻译
     *
     * @param block 方块名，大写命名空间
     * @return 中文翻译
     */
    public static String getValue(String block) throws Exception {
//        String key = Objects.requireNonNull(Material.getMaterial(block)).getTranslationKey();
        String key = Objects.requireNonNull(Material.getMaterial(block)).getBlockTranslationKey();
        File file = new File(Main.getInstance().getDataFolder(), "zh_cn.json");
        FileReader reader = new FileReader(file);
        JSONParser parser = new JSONParser();
        Object object = parser.parse(reader);
        JSONObject jsonObject = (JSONObject) object;
        return (String) jsonObject.get(key);
    }
}
