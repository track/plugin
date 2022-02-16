package net.analyse.plugin.commands.subcommands;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.commands.SubCommand;
import net.analyse.plugin.request.PluginAPIRequest;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;

public class SetupCommand extends SubCommand {

    public SetupCommand(final @NotNull AnalysePlugin plugin) {
        super(plugin, "setup", "analyse.setup");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.parse("&cYou must specify a server token."));
            return;
        }

        final String serverToken = args[0];

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final PluginAPIRequest apiRequest = new PluginAPIRequest("server");

            apiRequest.getRequest()
                    .header("Content-Type", "application/json")
                    .header("X-SERVER-TOKEN", serverToken);

            final HttpResponse<String> httpResponse = apiRequest.send();
            try {
                final JsonObject bodyJson = AnalysePlugin.GSON.fromJson(httpResponse.body(), JsonObject.class);

                if (httpResponse.statusCode() == 200) {
                    final JsonObject serverJson = bodyJson.getAsJsonObject("data");
                    sender.sendMessage(plugin.parse("&7Successfully setup server with token &b" + serverJson.get("name").getAsString() + "&7."));
                    plugin.getConfig().set("server.token", serverToken);
                    plugin.getConfig().set("server.id", serverJson.get("uuid").getAsString());
                    plugin.saveConfig();
                    plugin.setSetup(true);
                } else {
                    sender.sendMessage(plugin.parse("&b[Analyse] &7Sorry, but that server token isn't valid."));
                }
            } catch (JsonSyntaxException e) {
                sender.sendMessage(plugin.parse("&b[Analyse] &7Sorry, but that server token isn't valid."));
            }
        });
    }
}
