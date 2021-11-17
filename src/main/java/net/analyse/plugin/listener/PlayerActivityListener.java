package net.analyse.plugin.listener;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.request.PlayerSessionRequest;
import net.analyse.plugin.request.PluginAPIRequest;
import net.analyse.plugin.request.object.PlayerStatistic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.List;

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

        // TODO: Make this dynamic.
        List<PlayerStatistic> playerStatistics = enabledStats.stream().map(enabledStat -> new PlayerStatistic(enabledStat, Math.random() * 10)).toList();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerSessionRequest playerSessionRequest = new PlayerSessionRequest(
                    player.getUniqueId(), // uuid
                    player.getName(), // username
                    plugin.getActiveJoinMap().getOrDefault(player.getUniqueId(), null), // get time they joined at
                    new Date(), // the time they quit at
                    plugin.getPlayerDomainMap().getOrDefault(player.getUniqueId(), null), // get domain used
                    player.getAddress().getHostString(), // player ip
                    playerStatistics // their stats
            );

            plugin.getActiveJoinMap().remove(player.getUniqueId());
            plugin.getPlayerDomainMap().remove(player.getUniqueId());

            PluginAPIRequest apiRequest = new PluginAPIRequest("server/sessions");

            apiRequest.getRequest()
                    .header("Content-Type", "application/json")
                    .header("X-ANALYSE-TOKEN", plugin.getConfig().getString("server-token"))
                    .POST(HttpRequest.BodyPublishers.ofString(playerSessionRequest.toJSON()));

            HttpResponse<String> httpResponse = apiRequest.send();

            System.out.println(httpResponse.body());
        });
    }

}
