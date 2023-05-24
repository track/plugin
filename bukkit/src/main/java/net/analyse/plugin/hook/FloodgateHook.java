package net.analyse.plugin.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class FloodgateHook {
    public static boolean isBedrock(Player player) {
        if (!Bukkit.getPluginManager().isPluginEnabled("floodgate"))
            return false;

        return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
    }
}
