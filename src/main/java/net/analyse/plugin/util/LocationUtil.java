package net.analyse.plugin.util;

import com.google.gson.JsonObject;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.request.PluginAPIRequest;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LocationUtil {
    private AnalysePlugin plugin;

    public LocationUtil(final @NotNull AnalysePlugin plugin) {
        this.plugin = plugin;
    }

    public String fromIp(final @NotNull String ip) {
        Response response = new PluginAPIRequest("ip/" + ip)
                .withServerToken(plugin.getConfig().getString("server.token"))
                .send();

        if (response.code() == 404) {
            return null;
        }

        try {
            final JsonObject bodyJson = AnalysePlugin.GSON.fromJson(response.body().string(), JsonObject.class);
            response.close();
            return bodyJson.get("country_code").getAsString();
        } catch (IOException e) {
            response.close();
            return null;
        }

    }
}
