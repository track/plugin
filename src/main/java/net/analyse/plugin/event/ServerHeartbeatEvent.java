package net.analyse.plugin.event;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.request.PluginAPIRequest;
import net.analyse.plugin.request.impl.ServerHeartbeatRequest;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ServerHeartbeatEvent implements Runnable {

    private final AnalysePlugin plugin;

    public ServerHeartbeatEvent(final @NotNull AnalysePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!plugin.isSetup()) {
            return;
        }

        ServerHeartbeatRequest serverHeartbeatRequest = new ServerHeartbeatRequest(Bukkit.getOnlinePlayers().size());

        Response response = new PluginAPIRequest("server/heartbeat")
                .withPayload(serverHeartbeatRequest.toJson())
                .withServerToken(plugin.getConfig().getString("server.token"))
                .send();

        if (response.code() == 404) {
            plugin.getLogger().severe("The server that was configured no longer exists!");
            plugin.setSetup(false);
        }

        response.close();
    }
}
