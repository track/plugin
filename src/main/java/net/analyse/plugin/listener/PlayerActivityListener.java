package net.analyse.plugin.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.util.Config;
import net.analyse.sdk.exception.ServerNotFoundException;
import net.analyse.sdk.request.object.PlayerStatistic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerActivityListener implements Listener {

    private final AnalysePlugin plugin;

    public PlayerActivityListener(final @NotNull AnalysePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(final @NotNull PlayerLoginEvent event) {
        if (!plugin.isSetup()) return;

        if (Config.EXCLUDED_PLAYERS.contains(event.getPlayer().getUniqueId().toString())) return;

        plugin.debug("Player connecting via: " + event.getHostname());

        plugin.getPlayerDomainMap().put(event.getPlayer().getUniqueId(), event.getHostname());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.isSetup()) return;

        if (Config.EXCLUDED_PLAYERS.contains(event.getPlayer().getUniqueId().toString())) return;

        plugin.debug("Tracking " + event.getPlayer().getName() + " to current time");

        plugin.getActiveJoinMap().put(event.getPlayer().getUniqueId(), new Date());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (!plugin.isSetup()) return;

        if (Config.EXCLUDED_PLAYERS.contains(event.getPlayer().getUniqueId().toString())) return;

        final Player player = event.getPlayer();

        final List<PlayerStatistic> playerStatistics = new ArrayList<>();


        if (plugin.isPapiHooked()) {
            for (String placeholder : Config.ENABLED_STATS) {
                String resolvedPlaceholder = PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%");

                if (!resolvedPlaceholder.equalsIgnoreCase("%" + placeholder + "%")) {
                    playerStatistics.add(new PlayerStatistic(placeholder, resolvedPlaceholder));
                }
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final UUID playerUuid = player.getUniqueId();
            final String playerName = player.getName();
            final Date joinedAt = plugin.getActiveJoinMap().getOrDefault(playerUuid, null);
            final String domainConnected = plugin.getPlayerDomainMap().getOrDefault(playerUuid, null);
            final String playerIp = Objects.requireNonNull(player.getAddress()).getHostString();
            final Date quitAt = new Date();
            long seconds = (quitAt.getTime()-joinedAt.getTime()) / 1000;

            if(seconds > Config.MIN_SESSION_DURATION) {
                try {
                    plugin.getCore().sendPlayerSession(playerUuid, playerName, joinedAt, domainConnected, playerIp, playerStatistics);
                    plugin.debug(String.format("%s (%s) disconnected, who joined at %s and connected %s with IP of %s", playerName, playerUuid, joinedAt, (domainConnected != null ? "via " + domainConnected : "directly"), playerIp));
                } catch (ServerNotFoundException e) {
                    plugin.setSetup(false);
                    plugin.getLogger().warning("The server specified no longer exists.");
                }
            }

            plugin.getActiveJoinMap().remove(player.getUniqueId());
            plugin.getPlayerDomainMap().remove(player.getUniqueId());
        });
    }

}
