package net.analyse.plugin.event;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.sdk.exception.ServerNotFoundException;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ServerHeartbeatEvent implements Runnable {

    private final AnalysePlugin plugin;

    public ServerHeartbeatEvent(final @NotNull AnalysePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!plugin.isSetup()) return;

        try {
            plugin.getCore().sendHeartbeat(Bukkit.getOnlinePlayers().size());
        } catch (ServerNotFoundException e) {
            plugin.setSetup(false);
            plugin.getLogger().warning("The server specified no longer exists.");
        }
    }
}
