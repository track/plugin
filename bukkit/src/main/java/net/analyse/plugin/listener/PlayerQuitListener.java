package net.analyse.plugin.listener;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.event.type.AnalysePlayerQuitEvent;
import net.analyse.sdk.exception.ServerNotFoundException;
import net.analyse.sdk.obj.AnalysePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Level;

public class PlayerQuitListener implements Listener {
    private final AnalysePlugin platform;

    public PlayerQuitListener(AnalysePlugin platform) {
        this.platform = platform;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player bukkitPlayer = event.getPlayer();
        AnalysePlayer player = platform.getPlayers().get(bukkitPlayer.getUniqueId());

        if(player == null) return;

        platform.updatePlaceholderAPIStatistics(bukkitPlayer, player.getStatistics());
        platform.debug("Preparing to track " + bukkitPlayer.getName() + "..");

        if (new AnalysePlayerQuitEvent(bukkitPlayer, player).call()) {
            platform.debug("Not tracking player session for " + player.getName() + " as AnalysePlayerTrackSessionEvent was cancelled.");
            platform.getPlayers().remove(player.getUniqueId());
            return;
        }

        platform.getSDK().trackPlayerSession(player).thenAccept(successful -> {
            if(! successful) {
                platform.warning("Failed to track player session for " + player.getName() + ".");
                return;
            }

            platform.debug("Successfully tracked player session for " + player.getName() + ".");
        }).exceptionally(ex -> {
            Throwable cause = ex.getCause();
            platform.log(Level.WARNING, "Failed to track player session: " + cause.getMessage());

            if(cause instanceof ServerNotFoundException) {
                platform.halt();
            } else {
                cause.printStackTrace();
            }

            return null;
        });
    }
}
