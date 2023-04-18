package net.analyse.plugin.event;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.sdk.obj.AnalysePlayer;
import net.analyse.sdk.platform.PlatformConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Date;

public class PlayerJoinListener implements Listener {
    private final AnalysePlugin platform;

    public PlayerJoinListener(AnalysePlugin platform) {
        this.platform = platform;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player bukkitPlayer = event.getPlayer();

        PlatformConfig analyseConfig = platform.getPlatformConfig();
        if(analyseConfig.isPlayerExcluded(bukkitPlayer.getUniqueId())) {
            platform.debug("Skipped tracking " + bukkitPlayer.getName() + " as they are an excluded player.");
            return;
        }

        AnalysePlayer player = new AnalysePlayer(
                bukkitPlayer.getName(),
                bukkitPlayer.getUniqueId(),
                bukkitPlayer.getAddress() != null ? bukkitPlayer.getAddress().getAddress().getHostAddress() : null
        );

        if(bukkitPlayer.getAddress() != null) {
            player.setDomain(bukkitPlayer.getAddress().getHostName());
        }

        platform.debug("Tracking " + bukkitPlayer.getName() + " that connected via: " + player.getDomain());

        if(analyseConfig.shouldUseServerFirstJoinedAt()) {
            player.setFirstJoinedAt(new Date(bukkitPlayer.getFirstPlayed()));
        }

        platform.getSDK().getCountryFromIp(player.getIpAddress()).thenAccept(player::setCountry);

        platform.getPlayers().put(bukkitPlayer.getUniqueId(), player);
    }
}
