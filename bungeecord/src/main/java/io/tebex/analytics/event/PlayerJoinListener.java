package io.tebex.analytics.event;

import io.tebex.analytics.AnalysePlugin;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final AnalysePlugin plugin;

    public PlayerJoinListener(AnalysePlugin plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onJoin(LoginEvent event) {
        InetSocketAddress virtualDomain = event.getConnection().getVirtualHost();
        if(virtualDomain == null) return;

        String hostName = virtualDomain.getHostName();
        UUID uniqueId = event.getConnection().getUniqueId();

        plugin.getPlayerDomains().put(uniqueId, hostName);
    }
}
