package net.analyse.plugin.manager;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.sdk.exception.ServerNotFoundException;
import net.analyse.sdk.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;

public class HeartbeatManager {
    private final AnalysePlugin platform;
    private BukkitTask task;

    public HeartbeatManager(AnalysePlugin platform) {
        this.platform = platform;
    }

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

    public void stop() {
        if (task == null) return;
        task.cancel();
    }
}
