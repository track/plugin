package net.analyse.plugin.commands.subcommands;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.commands.SubCommand;
import net.analyse.sdk.AnalyseSDK;
import net.analyse.sdk.exception.ServerNotFoundException;
import net.analyse.sdk.response.GetServerResponse;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SetupCommand extends SubCommand {

    public SetupCommand(final @NotNull AnalysePlugin plugin) {
        super(plugin, "setup", "analyse.setup");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (plugin.isSetup()) {
            sender.sendMessage(plugin.parse("&b[Analyse] &7This server has already been setup."));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(plugin.parse("&b[Analyse] &7You must specify a server token."));
            return;
        }

        final String serverToken = args[0];

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            AnalyseSDK analyseSDK = plugin.setup(serverToken);

            try {
                GetServerResponse server = analyseSDK.getServer();
                sender.sendMessage(plugin.parse("&b[Analyse] &7Successfully setup server with token &b" + server.getName() + "&7."));

                plugin.getConfig().set("server.token", serverToken);
                plugin.getConfig().set("server.id", server.getUuid());
                plugin.saveConfig();
                plugin.setSetup(true);
            } catch (ServerNotFoundException e) {
                sender.sendMessage(plugin.parse("&b[Analyse] &7Sorry, but that server token isn't valid."));
            }
        });
    }
}
