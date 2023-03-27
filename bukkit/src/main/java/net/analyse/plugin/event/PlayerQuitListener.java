package net.analyse.plugin.event;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.sdk.obj.AnalysePlayer;
import net.analyse.sdk.request.exception.AnalyseException;
import net.analyse.sdk.request.exception.ServerNotFoundException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Level;

/**
 * Listens for player leave events.
 */
public class PlayerQuitListener implements Listener {
    private final AnalysePlugin platform;

    public PlayerQuitListener(AnalysePlugin platform) {
        this.platform = platform;
    }

    /**
     * Handles the player leave event.
     * @param event The event.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player bukkitPlayer = event.getPlayer();

        AnalysePlayer player = platform.getPlayers().get(bukkitPlayer.getUniqueId());
        platform.debug("Finalising tracking for " + bukkitPlayer.getName() + "..");
        if(player == null) return;

        try {
            platform.debug("Sending session for " + bukkitPlayer.getName() + " to Analyse..");
            platform.getSDK().trackPlayerSession(player);
        } catch (AnalyseException | ServerNotFoundException e) {
            if(e instanceof ServerNotFoundException) platform.halt();
            platform.log(Level.WARNING, "Failed to track player session: " + e.getMessage());
        }
    }
}
