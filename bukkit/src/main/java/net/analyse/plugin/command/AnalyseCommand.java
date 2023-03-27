package net.analyse.plugin.command;

import com.google.common.collect.ImmutableList;
import net.analyse.plugin.manager.CommandManager;
import net.analyse.sdk.platform.command.PlatformCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class AnalyseCommand implements TabExecutor {
    private final CommandManager commandManager;

    /**
     * Constructor for the AnalyseCommand class
     * @param commandManager The command manager
     */
    public AnalyseCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Called when a player executes a command
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("analyse.admin")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[Analyse] &7You do not have access to that command."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[Analyse] &7Plugin information:"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(" &b- &7Version: &bv%s&7.", commandManager.getPlatform().getDescription().getVersion())));
            return true;
        }

        Map<String, Map<String, PlatformCommand>> commandsByModule = commandManager.getCommandsByModule();
        Optional<PlatformCommand> subCommand = commandsByModule.values().stream()
                .flatMap(commandsMap -> commandsMap.values().stream())
                .filter(cmd -> cmd.getName().equalsIgnoreCase(args[0]))
                .findFirst();

        if (!subCommand.isPresent()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&8[Analyse] &7Unknown sub command: &b%s&7.", args[0].toLowerCase())));
            return true;
        }

        if (!sender.hasPermission("analyse." + subCommand.get().getName())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[Analyse] &7You do not have access to that command."));
            return true;
        }

        subCommand.get().execute(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    /**
     * Called when a player tab-completes a command
     * @param sender CommandSender which represents the player
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args The arguments passed to the command, including final
     *     partial argument to be completed
     * @return A List of possible completions for the final argument, or none
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return args.length == 1 ?
                commandManager.getCommandsByModule().values().stream()
                        .flatMap(commandsMap -> commandsMap.keySet().stream())
                        .filter(s -> s.startsWith(args[0]))
                        .collect(Collectors.toList()) :
                ImmutableList.of();
    }
}