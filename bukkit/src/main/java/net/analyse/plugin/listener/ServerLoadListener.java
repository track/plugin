package net.analyse.plugin.listener;

import net.analyse.plugin.AnalysePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerLoadListener implements Listener {
    private final AnalysePlugin platform;

    public ServerLoadListener(AnalysePlugin platform) {
        this.platform = platform;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        HandlerList.unregisterAll(this);
        platform.loadModules();
    }
}
