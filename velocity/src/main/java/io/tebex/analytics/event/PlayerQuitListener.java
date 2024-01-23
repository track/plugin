package io.tebex.analytics.event;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import io.tebex.analytics.AnalysePlugin;

public class PlayerQuitListener {
    private final AnalysePlugin plugin;

    public PlayerQuitListener(AnalysePlugin plugin) {
        this.plugin = plugin;
        plugin.getProxy().getEventManager().register(plugin, this);
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        plugin.getPlayerDomains().remove(event.getPlayer().getUniqueId());
    }
}
