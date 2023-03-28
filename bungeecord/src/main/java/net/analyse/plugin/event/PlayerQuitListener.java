package net.analyse.plugin.event;

import net.analyse.plugin.AnalysePlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerQuitListener implements Listener {
    private final AnalysePlugin plugin;

    public PlayerQuitListener(AnalysePlugin plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        plugin.getPlayerDomains().remove(player.getUniqueId());
        plugin.log(player.getName() + " disconnected from the proxy");
    }
}
