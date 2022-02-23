package net.analyse.plugin.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.request.impl.PlayerSessionRequest;
import net.analyse.plugin.request.PluginAPIRequest;
import net.analyse.plugin.request.object.PlayerStatistic;
import net.analyse.plugin.util.Config;
import net.analyse.plugin.util.EncryptUtil;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PlayerActivityListener implements Listener {

    private final AnalysePlugin plugin;

    public PlayerActivityListener(final @NotNull AnalysePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(final @NotNull PlayerLoginEvent event) {
        if (!plugin.isSetup()) {
            return;
        }

        if (Config.EXCLUDED_PLAYERS.contains(event.getPlayer().getUniqueId().toString())) {
            return;
        }

        if (Config.DEBUG) {
            plugin.getLogger().info("Player connecting via: " + event.getHostname());
        }

        plugin.getPlayerDomainMap().put(event.getPlayer().getUniqueId(), event.getHostname());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.isSetup()) {
            return;
        }

        if (Config.EXCLUDED_PLAYERS.contains(event.getPlayer().getUniqueId().toString())) {
            return;
        }

        if (Config.DEBUG) {
            plugin.getLogger().info("Tracking " + event.getPlayer().getName() + " to current time");
        }

        plugin.getActiveJoinMap().put(event.getPlayer().getUniqueId(), new Date());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (!plugin.isSetup()) {
            return;
        }

        if (Config.EXCLUDED_PLAYERS.contains(event.getPlayer().getUniqueId().toString())) {
            return;
        }

        final Player player = event.getPlayer();

        final List<PlayerStatistic> playerStatistics = new ArrayList<>();

        final String playerIp = Objects.requireNonNull(player.getAddress()).getHostString();
        final String ipCountry = plugin.locationUtil().fromIp(playerIp);
        final String ipHashed = EncryptUtil.toSHA256(playerIp, plugin.getEncryptionKey().getBytes());

        if (plugin.isPapiHooked()) {
            for (String placeholder : Config.ENABLED_STATS) {
                String resolvedPlaceholder = PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%");

                if (!resolvedPlaceholder.equalsIgnoreCase("%" + placeholder + "%")) {
                    playerStatistics.add(new PlayerStatistic(placeholder, resolvedPlaceholder));
                }
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerSessionRequest playerSessionRequest = new PlayerSessionRequest(
                    player.getUniqueId(),
                    player.getName(),
                    plugin.getActiveJoinMap().getOrDefault(player.getUniqueId(), null),
                    new Date(),
                    plugin.getPlayerDomainMap().getOrDefault(player.getUniqueId(), null),
                    ipHashed,
                    ipCountry,
                    playerStatistics
            );

            plugin.getActiveJoinMap().remove(player.getUniqueId());
            plugin.getPlayerDomainMap().remove(player.getUniqueId());

            Response response = new PluginAPIRequest("server/sessions")
                    .withPayload(playerSessionRequest.toJson())
                    .withServerToken(plugin.getConfig().getString("server.token"))
                    .send();

            response.close();
        });
    }

}
