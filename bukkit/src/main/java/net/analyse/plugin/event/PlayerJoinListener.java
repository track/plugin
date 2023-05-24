package net.analyse.plugin.event;

import com.google.common.collect.Maps;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.hook.FloodgateHook;
import net.analyse.sdk.obj.AnalysePlayer;
import net.analyse.sdk.platform.PlatformConfig;
import net.analyse.sdk.platform.PlayerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final AnalysePlugin platform;
    private final Map<UUID, String> joinMap;

    public PlayerJoinListener(AnalysePlugin platform) {
        this.platform = platform;
        joinMap = Maps.newConcurrentMap();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        Player bukkitPlayer = event.getPlayer();

        PlatformConfig analyseConfig = platform.getPlatformConfig();
        if(analyseConfig.isPlayerExcluded(bukkitPlayer.getUniqueId())) {
            return;
        }

        joinMap.put(bukkitPlayer.getUniqueId(), event.getHostname());
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

        if(joinMap.containsKey(bukkitPlayer.getUniqueId())) {
            player.setDomain(joinMap.get(bukkitPlayer.getUniqueId()));
            joinMap.remove(bukkitPlayer.getUniqueId());
        }

        if(analyseConfig.shouldUseServerFirstJoinedAt()) {
            player.setFirstJoinedAt(new Date(bukkitPlayer.getFirstPlayed()));
        }

        // Bedrock Tracking
        if (!analyseConfig.isBedrockFloodgateHook()) {
            if (analyseConfig.getBedrockPrefix() != null && player.getName().startsWith(analyseConfig.getBedrockPrefix())) {
                player.setType(PlayerType.BEDROCK);
            }
        } else if (FloodgateHook.isBedrock(event.getPlayer())) {
            player.setType(PlayerType.BEDROCK);
        }

        platform.debug("Tracking " + bukkitPlayer.getName() + " (" + player.getType() + ") that connected via: " + player.getDomain());

        platform.updatePlaceholderAPIStatistics(bukkitPlayer, player.getStatistics());

        platform.getSDK().getCountryFromIp(player.getIpAddress()).thenAccept(player::setCountry);

        platform.getPlayers().put(bukkitPlayer.getUniqueId(), player);
    }
}
