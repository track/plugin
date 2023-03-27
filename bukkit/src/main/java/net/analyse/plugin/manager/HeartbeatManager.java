package net.analyse.plugin.manager;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.sdk.exception.ServerNotFoundException;
import net.analyse.sdk.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;

/**
 * The heartbeat manager is responsible for sending a heartbeat to Analyse
 */
public class HeartbeatManager {
    private final AnalysePlugin platform;
    private BukkitTask task;

    /**
     * Create a new heartbeat manager.
     * @param platform The platform.
     */
    public HeartbeatManager(AnalysePlugin platform) {
        this.platform = platform;
    }

    /**
     * Start sending heartbeats.
     */
    public void start() {
        task = platform.getServer().getScheduler().runTaskTimer(platform, () -> {
            int playerCount = Bukkit.getOnlinePlayers().size();

            if(playerCount == 0) {
                platform.debug("Not sending heartbeat as there are no players online.");
                return;
            }

            platform.getSDK().trackHeartbeat(playerCount).thenAccept(successful -> {
                if(! successful) {
                    platform.warning("Failed to send server heartbeat.");
                    return;
                }

                platform.debug("Successfully sent server heartbeat.");
            }).exceptionally(ex -> {
                Throwable cause = ex.getCause();
                platform.log(Level.WARNING, "Failed to track server heartbeat: " + cause.getMessage());

                if(cause instanceof ServerNotFoundException) {
                    platform.halt();
                } else {
                    cause.printStackTrace();
                }

                return null;
            });
        }, 0, 20 * 60);
    }

    /**
     * Stop sending heartbeats.
     */
    public void stop() {
        if (task == null) return;
        task.cancel();
    }
}
