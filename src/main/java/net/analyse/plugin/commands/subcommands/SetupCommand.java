package net.analyse.plugin.commands.subcommands;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.commands.SubCommand;
import net.analyse.plugin.request.PluginAPIRequest;
import okhttp3.Response;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

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

            Response response = new PluginAPIRequest("server")
                    .withServerToken(serverToken)
                    .send();

            try {
                final JsonObject bodyJson = AnalysePlugin.GSON.fromJson(response.body().string(), JsonObject.class);

                if (response.code() == 200) {
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
            } catch (IOException e) {
                sender.sendMessage(plugin.parse("&b[Analyse] &7Sorry, an error occurred when setting up."));
            }
        });
    }
}
