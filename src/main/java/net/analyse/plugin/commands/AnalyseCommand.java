package net.analyse.plugin.commands;

import com.google.common.collect.ImmutableList;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.commands.subcommands.ReloadCommand;
import net.analyse.plugin.commands.subcommands.SetupCommand;
import net.analyse.sdk.exception.ServerNotFoundException;
import net.analyse.sdk.response.GetServerResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AnalyseCommand implements CommandExecutor {

    private final AnalysePlugin plugin;
    private final Map<String, SubCommand> commands = new HashMap<>();

    public AnalyseCommand(final @NotNull AnalysePlugin plugin) {
        this.plugin = plugin;

        ImmutableList.of(new SetupCommand(plugin), new ReloadCommand(plugin)).forEach(command -> commands.put(command.getName(), command));
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String label, final String[] args) {
        if (args.length == 0 || !commands.containsKey(args[0].toLowerCase())) {
            sender.sendMessage(" ");
            sender.sendMessage(plugin.parse("&b[Analyse] &7Plugin Information:"));
            sender.sendMessage(plugin.parse(String.format(" &b- &7Version: &bv%s&7.", plugin.getDescription().getVersion())));

            GetServerResponse server;
            if(plugin.isSetup()) {
                try {
                    server = plugin.getCore().getServer();
                    sender.sendMessage(plugin.parse(String.format(" &b- &7Linked to: &b%s&7.", server.getName())));
                    sender.sendMessage(plugin.parse(String.format(" &b- &7You've used &b%s &7of your &b%s &7quota limit.", server.getCurrentTeamQuota(), server.getTeamQuotaLimit())));
                } catch (ServerNotFoundException e) {
                    sender.sendMessage(plugin.parse("&b[Analyse] &7The server linked no longer exists."));
                    plugin.setSetup(false);
                }
            } else {
                sender.sendMessage(plugin.parse("&b[Analyse] &7You've not yet linked a server&7."));
            }

            sender.sendMessage(" ");

            return true;
        }

        final SubCommand subCommand = commands.get(args[0].toLowerCase());
        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(plugin.parse("&b[Analyse] &7You do not have access to that command."));
            return true;
        }

        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }
}
