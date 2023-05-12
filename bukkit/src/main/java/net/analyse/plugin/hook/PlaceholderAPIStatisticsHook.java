package net.analyse.plugin.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAPIStatisticsHook {
    public static String getStatistic(Player player, String statistic) {
        return PlaceholderAPI.setPlaceholders(player, "%" + statistic + "%");
    }
}
