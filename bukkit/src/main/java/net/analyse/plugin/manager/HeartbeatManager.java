package net.analyse.plugin.manager;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.sdk.request.exception.ServerNotFoundException;
import net.analyse.sdk.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;

/**
 * The heartbeat manager is responsible for sending a heartbeat to the Analyse
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

            platform.getSDK().trackHeartbeat(playerCount).whenComplete((successful, throwable) -> {
                if(throwable != null) {
                    Throwable cause = throwable.getCause();
                    if(cause instanceof ServerNotFoundException) this.stop();
                    platform.log(Level.WARNING, "Failed to send heartbeat: " + cause.getMessage());
                }

                if(! successful) {
                    platform.log(Level.WARNING, "Failed to send heartbeat!");
                    return;
                }

                platform.debug("Sending heartbeat with " + playerCount + " " + StringUtil.pluralise(playerCount, "player", "players") + ".");
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
