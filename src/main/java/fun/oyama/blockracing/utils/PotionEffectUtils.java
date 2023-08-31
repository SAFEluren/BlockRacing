package fun.oyama.blockracing.utils;


import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class PotionEffectUtils {
    public static void add(Player player, PotionEffectType type, int duration, int amplifier) {
        if (player.hasPotionEffect(type)) {
            player.removePotionEffect(type);
        }
        player.addPotionEffect(new PotionEffect(type, duration, amplifier, false, true, true));
    }
}