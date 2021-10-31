package net.analyse.plugin.listener;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.request.PlayerSessionRequest;
import net.analyse.plugin.request.object.PlayerStatistic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.List;

public class PlayerActivityListener implements Listener {

    private final AnalysePlugin plugin;

    public PlayerActivityListener(AnalysePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getLogger().info("Tracking " + event.getPlayer().getName() + " to current time");
        plugin.getActiveJoinMap().put(event.getPlayer().getUniqueId(), new Date());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
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
                    playerStatistics // their stats
            );

            plugin.getActiveJoinMap().remove(player.getUniqueId());

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:8000/api/test"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(playerSessionRequest.toJSON()))
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
            } catch (IOException e) {
                // TODO: Handle this.
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO: Handle this.
                e.printStackTrace();
            }
        });
    }

}
