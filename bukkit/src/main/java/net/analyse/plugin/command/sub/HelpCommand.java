package net.analyse.plugin.command.sub;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.sdk.platform.command.PlatformCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HelpCommand extends PlatformCommand {
    private static final String COMMAND_NAME = "help";
    private static final String COMMAND_DESCRIPTION = "Provides information about the plugin's sub-commands.";
    private static final int COMMANDS_PER_PAGE = 10;

    public HelpCommand(AnalysePlugin platform) {
        super(COMMAND_NAME, COMMAND_DESCRIPTION, commandContext -> {
            CommandSender sender = (CommandSender) commandContext.getSender();
            String[] args = commandContext.getArguments();

            // Sorts sub-commands by module name, with the Analyse built-in commands always at the top.
            Map<String, Map<String, PlatformCommand>> commands = platform.getCommandManager().getCommandsByModule().entrySet().stream()
                    .sorted(Comparator.comparing(entry -> {
                        String key = entry.getKey();
                        return key.equalsIgnoreCase("Analyse") ? "" : key;
                    }, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new));

            int currentPage;
            int totalCommands = commands.values().stream().mapToInt(Map::size).sum();
            int totalPages = (totalCommands + COMMANDS_PER_PAGE - 1) / COMMANDS_PER_PAGE;

            if (args.length == 0) {
                currentPage = 1;

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[Analyse] &7Commands:"));
                int i = 0;
                for (Map<String, PlatformCommand> moduleCommands : commands.values()) {
                    for (PlatformCommand command : moduleCommands.values()) {
                        if (COMMANDS_PER_PAGE * (currentPage - 1) <= i) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&7- &b%s &8- &7%s", command.getName(), command.getDescription())));
                        }
                        if (++i % COMMANDS_PER_PAGE == 0) {
                            currentPage++;
                            break;
                        }
                    }
                    if (currentPage > totalPages) {
                        break;
                    }
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&7Page %d/%d. Use &b/analyse help (page)&7.", currentPage, totalPages)));
                return;
            }

            try {
                currentPage = Integer.parseInt(args[0]);
                if (currentPage <= 0 || currentPage > totalPages) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[Analyse] &7Invalid page number."));
                return;
            }

            int startIndex = (currentPage - 1) * COMMANDS_PER_PAGE;
            int endIndex = Math.min(startIndex + COMMANDS_PER_PAGE, totalCommands);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[Analyse] &7Commands:"));

            int i = 0;
            for (Map<String, PlatformCommand> moduleCommands : commands.values()) {
                if (i >= endIndex) {
                    break;
                }

                for (PlatformCommand command : moduleCommands.values()) {
                    if (i >= startIndex) {
                        String message = String.format("&7- &b%s &8- &7%s", command.getName(), command.getDescription());
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                    i++;

                    if (i >= endIndex) {
                        break;
                    }
                }
            }

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&7Page %d/%d. Use &b/analyse help (page)&7.", currentPage, totalPages)));
        });
    }
}