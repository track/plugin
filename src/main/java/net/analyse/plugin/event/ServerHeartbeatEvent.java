package net.analyse.plugin.event;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.request.PluginAPIRequest;
import net.analyse.plugin.request.ServerHeartbeatRequest;
import net.analyse.plugin.request.object.PlayerStatistic;
import org.bukkit.Bukkit;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class ServerHeartbeatEvent implements Event {

    private AnalysePlugin plugin;

    public ServerHeartbeatEvent(AnalysePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if(!plugin.isSetup()) return;

        plugin.getLogger().info("Sending a heartbeat..");

        ServerHeartbeatRequest serverHeartbeatRequest = new ServerHeartbeatRequest(Bukkit.getOnlinePlayers().size());

        PluginAPIRequest apiRequest = new PluginAPIRequest("server/heartbeat");

        apiRequest.getRequest()
                .header("Content-Type", "application/json")
                .header("X-SERVER-TOKEN", plugin.getConfig().getString("server-token"))
                .POST(HttpRequest.BodyPublishers.ofString(serverHeartbeatRequest.toJSON()));

        HttpResponse<String> httpResponse = apiRequest.send();

        if(httpResponse.statusCode() == 404) {
            plugin.getLogger().severe("The server that was configured no longer exists!");
            plugin.setSetup(false);
            return;
        }

        System.out.println(httpResponse.body());
    }
}
