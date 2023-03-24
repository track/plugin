package net.analyse.plugin.manager;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.sdk.request.exception.AnalyseException;
import net.analyse.sdk.request.exception.ServerNotFoundException;
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
        task = platform.getServer().getScheduler().runTaskTimerAsynchronously(platform, () -> {
            try {
                int playerCount = Bukkit.getOnlinePlayers().size();

                if(playerCount == 0) {
                    platform.debug("Not sending heartbeat as there are no players online.");
                    return;
                }

                platform.debug("Sending heartbeat with " + playerCount + " " + StringUtil.pluralise(playerCount, "player", "players") + ".");
                platform.getSDK().trackHeartbeat(playerCount);
            } catch (AnalyseException | ServerNotFoundException e) {
                if(e instanceof ServerNotFoundException) this.stop();
                platform.log(Level.WARNING, "Failed to send heartbeat: " + e.getMessage());
            }
        }, 0, 20 * 60);
    }

    public void stop() {
        if (task == null) return;
        task.cancel();
    }
}
