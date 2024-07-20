package io.tebex.analytics.manager;

import io.tebex.analytics.AnalyticsPlugin;
import io.tebex.analytics.sdk.exception.ServerNotFoundException;
import org.bukkit.Bukkit;
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.util.logging.Level;

public class HeartbeatManager {
    private final AnalyticsPlugin platform;
    private ScheduledTask task;

    public HeartbeatManager(AnalyticsPlugin platform) {
        this.platform = platform;
    }

    public void start() {
        task = platform.getScheduler().globalRegionalScheduler().runAtFixedRate(() -> {
            if(! platform.isSetup()) return;

            int playerCount = this.platform.getPlayerCountService().getPlayerCount();

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
        }, 1, 20 * 60);
    }

    public void stop() {
        if (task == null) return;
        task.cancel();
    }
}
