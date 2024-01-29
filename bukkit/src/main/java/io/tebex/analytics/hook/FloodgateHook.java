package io.tebex.analytics.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class FloodgateHook {
    public boolean isBedrock(Player player) {
        if(! Bukkit.getPluginManager().isPluginEnabled("floodgate")) return false;
        return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
    }
}