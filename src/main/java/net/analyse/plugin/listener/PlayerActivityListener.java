package net.analyse.plugin.listener;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.json.PlayerSessionRequest;
import net.analyse.plugin.json.object.PlayerStat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Date;

public class PlayerActivityListener {

    private final AnalysePlugin plugin;

    public PlayerActivityListener(AnalysePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PlayerSessionRequest playerSessionRequest = new PlayerSessionRequest(
                player.getUniqueId(), // uuid
                player.getName(), // username
                null, // get time they joined at
                new Date(), // the time they quit at
                Arrays.asList(new PlayerStat("magic", 32), new PlayerStat("kills", 300)) // their stats
        );

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
    }

}
