package net.analyse.plugin.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.request.PlayerSessionRequest;
import net.analyse.plugin.request.PluginAPIRequest;
import net.analyse.plugin.request.object.PlayerStatistic;
import net.analyse.plugin.util.EncryptUtil;
import net.analyse.plugin.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PlayerActivityListener implements Listener {

    private final AnalysePlugin plugin;

    public PlayerActivityListener(AnalysePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if(!plugin.isSetup()) return;
        if(plugin.getConfig().getStringList("excluded.players").contains(event.getPlayer().getUniqueId().toString())) return;

        plugin.getLogger().info("Player connecting via: " + event.getHostname());
        plugin.getPlayerDomainMap().put(event.getPlayer().getUniqueId(), event.getHostname());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!plugin.isSetup()) return;
        if(plugin.getConfig().getStringList("excluded.players").contains(event.getPlayer().getUniqueId().toString())) return;

        plugin.getLogger().info("Tracking " + event.getPlayer().getName() + " to current time");
        plugin.getActiveJoinMap().put(event.getPlayer().getUniqueId(), new Date());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(!plugin.isSetup()) return;
        if(plugin.getConfig().getStringList("excluded.players").contains(event.getPlayer().getUniqueId().toString())) return;

        Player player = event.getPlayer();

        List<String> enabledStats = plugin.getConfig().getStringList("enabled-stats");

        List<PlayerStatistic> playerStatistics = new ArrayList<>();

        String playerIp = Objects.requireNonNull(player.getAddress()).getHostString();
        String ipCountry = LocationUtil.fromIp(playerIp);
        String ipHashed = EncryptUtil.toSHA256(playerIp, plugin.getEncryptionKey().getBytes());

        if(plugin.isPapiHooked()) {
            for (String placeholder : enabledStats) {
                String resolvedPlaceholder = PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%");

                if(!resolvedPlaceholder.equalsIgnoreCase("%" + placeholder + "%")) {
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

            PluginAPIRequest apiRequest = new PluginAPIRequest("server/sessions");

            apiRequest.getRequest()
                    .header("X-SERVER-TOKEN", plugin.getConfig().getString("server.token"))
                    .POST(HttpRequest.BodyPublishers.ofString(playerSessionRequest.toJSON()));

            HttpResponse<String> httpResponse = apiRequest.send();
        });
    }

}
