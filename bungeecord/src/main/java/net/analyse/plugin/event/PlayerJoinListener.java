package net.analyse.plugin.event;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.sdk.util.MapperUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;

public class PlayerJoinListener implements Listener {
    private final AnalysePlugin plugin;

    public PlayerJoinListener(AnalysePlugin plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String ipAddress = player.getPendingConnection().getVirtualHost().getAddress().getHostAddress();

        plugin.log(player.getName() + " has connected from IP address: " + ipAddress);

        InetSocketAddress virtualDomain = player.getPendingConnection().getVirtualHost();
        if (virtualDomain != null) {
            String hostName = MapperUtil.mapVirtualDomainToPlayer(virtualDomain);

            plugin.log(player.getName() + " has connected from domain: " + hostName);
            plugin.getPlayerDomains().put(player.getUniqueId(), hostName);
        }
    }
}
