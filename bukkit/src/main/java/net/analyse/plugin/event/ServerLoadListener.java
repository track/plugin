package net.analyse.plugin.event;

import net.analyse.plugin.AnalysePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

/**
 * Listens for the server load event.
 */
public class ServerLoadListener implements Listener {
    private final AnalysePlugin platform;

    public ServerLoadListener(AnalysePlugin platform) {
        this.platform = platform;
    }

    /**
     * Called when the server is loaded.
     * @param event The event.
     */
    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        HandlerList.unregisterAll(this);

        // Loads platform modules.
        platform.loadModules();
    }
}
