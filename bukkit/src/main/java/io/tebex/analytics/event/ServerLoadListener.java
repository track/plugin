package io.tebex.analytics.event;

import io.tebex.analytics.AnalyticsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerLoadListener implements Listener {
    private final AnalyticsPlugin platform;

    public ServerLoadListener(AnalyticsPlugin platform) {
        this.platform = platform;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        HandlerList.unregisterAll(this);
        platform.loadModules();
    }
}
