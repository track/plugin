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
        InetSocketAddress virtualDomain = player.getPendingConnection().getVirtualHost();
        String domain = MapperUtil.mapVirtualDomainToPlayer(virtualDomain);
        plugin.getPlayerDomains().put(player.getUniqueId(), domain);

        plugin.log(player.getName() + " connected to the proxy from: " + domain);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        plugin.getPlayerDomains().remove(player.getUniqueId());
        plugin.log(player.getName() + " disconnected from the proxy");
    }
}
