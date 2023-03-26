package net.analyse.plugin.command;

import com.google.common.collect.ImmutableList;
import net.analyse.plugin.manager.CommandManager;
import net.analyse.sdk.platform.command.PlatformCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyseCommand implements TabExecutor {
    private CommandManager commandManager;

    public AnalyseCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[Analyse] &7Plugin information:"));
            return true;
        }

        Map<String, PlatformCommand> commands = commandManager.getCommands();

        if(!commands.containsKey(args[0].toLowerCase())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[Analyse] &7Unknown command."));
            return true;
        }

        final PlatformCommand subCommand = commands.get(args[0].toLowerCase());

        if(!sender.hasPermission("analyse." + subCommand.getName())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[Analyse] &7You do not have access to that command."));
            return true;
        }

        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 1) {
            return commandManager.getCommands()
                    .keySet()
                    .stream()
                    .filter(s -> s.startsWith(args[0]))
                    .collect(Collectors.toList());
        }

        return ImmutableList.of();
    }
}