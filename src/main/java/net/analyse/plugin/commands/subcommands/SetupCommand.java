package net.analyse.plugin.commands.subcommands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.commands.SubCommand;
import net.analyse.plugin.request.PluginAPIRequest;
import org.bukkit.entity.Player;

import java.net.http.HttpResponse;

public class SetupCommand extends SubCommand {

    public SetupCommand(AnalysePlugin plugin) {
        super(plugin, "setup", "analyse.setup");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(plugin.parse("&cYou must specify a server token."));
            return;
        }

        final String serverToken = args[0];

        PluginAPIRequest apiRequest = new PluginAPIRequest("server");

        apiRequest.getRequest()
                .header("Content-Type", "application/json")
                .header("X-SERVER-TOKEN", serverToken);

        HttpResponse<String> httpResponse = apiRequest.send();
        System.out.println(httpResponse.body());
        JsonObject bodyJson = new Gson().fromJson(httpResponse.body(), JsonObject.class);

        if(httpResponse.statusCode() == 200) {
            JsonObject serverJson = bodyJson.getAsJsonObject("data");
            player.sendMessage(plugin.parse("&7Successfully setup server with token &b" + serverJson.get("name").getAsString() + "&7."));
            plugin.getConfig().set("server.token", serverToken);
            plugin.getConfig().set("server.id", serverJson.get("uuid").getAsString());
            plugin.saveConfig();
            plugin.setSetup(true);
        } else {
            player.sendMessage(plugin.parse("&b[Analyse] &7Sorry, but that server token isn't valid."));
        }
    }

}
