package net.analyse.plugin.commands;

import net.analyse.plugin.commands.subcommands.*;
import net.analyse.plugin.AnalysePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AnalyseCommand implements CommandExecutor {

    private final AnalysePlugin plugin;
    private final Map<String, SubCommand> commands = new HashMap<>();

    public AnalyseCommand(AnalysePlugin plugin) {
        this.plugin = plugin;

        Arrays.asList(
                new SetupCommand(plugin)
        ).forEach(command -> commands.put(command.getName(), command));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;

        if (args.length == 0 || !commands.containsKey(args[0].toLowerCase())) {
            sender.sendMessage(plugin.parse("&b[Analyse] &7Running on &bv" + plugin.getDescription().getVersion() + "&7."));
            return true;
        }

        SubCommand subCommand = commands.get(args[0].toLowerCase());
        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(plugin.parse("&b[Analyse] &7You do not have access to that command."));
            return true;
        }

        subCommand.execute((Player) sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

}
