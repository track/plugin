package net.analyse.plugin.commands;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.commands.subcommands.SetupCommand;
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

        Collections.singletonList(new SetupCommand(plugin)).forEach(command -> {
            commands.put(command.getName(), command);
        });
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String label, final String[] args) {
        if (args.length == 0 || !commands.containsKey(args[0].toLowerCase())) {
            sender.sendMessage(plugin.parse("&b[Analyse] &7Running on &bv" + plugin.getDescription().getVersion() + "&7."));
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
