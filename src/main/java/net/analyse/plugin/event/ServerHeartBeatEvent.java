package net.analyse.plugin.event;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.request.PlayerSessionRequest;
import net.analyse.plugin.request.PluginAPIRequest;
import net.analyse.plugin.request.ServerHeartbeatRequest;
import net.analyse.plugin.request.object.PlayerStatistic;
import org.bukkit.Bukkit;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Date;

public class ServerHeartBeatEvent implements Event {

    private AnalysePlugin plugin;

    public ServerHeartBeatEvent(AnalysePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            ServerHeartbeatRequest serverHeartbeatRequest = new ServerHeartbeatRequest(Arrays.asList(new PlayerStatistic("online", Bukkit.getOnlinePlayers().size()), new PlayerStatistic("max", Bukkit.getMaxPlayers())));

            PluginAPIRequest apiRequest = new PluginAPIRequest("server/heartbeat");

            apiRequest.getRequest()
                    .header("Content-Type", "application/json")
                    .header("X-ANALYSE-TOKEN", plugin.getConfig().getString("server-token"))
                    .POST(HttpRequest.BodyPublishers.ofString(serverHeartbeatRequest.toJSON()));

            HttpResponse<String> httpResponse = apiRequest.send();
        });
    }
}
