package net.analyse.plugin.manager;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.command.AnalyseCommand;
import net.analyse.plugin.command.sub.DebugCommand;
import net.analyse.plugin.command.sub.HelpCommand;
import net.analyse.plugin.command.sub.SetupCommand;
import net.analyse.sdk.platform.command.PlatformCommand;
import org.bukkit.command.PluginCommand;

import java.util.*;

/**
 * A manager that registers built-in as priority, and then registers platform sub-commands.
 */
public class CommandManager {
    private final AnalysePlugin platform;
    private final Map<String, Map<String, PlatformCommand>> commandsByModule = new LinkedHashMap<>();

    /**
     * Creates a new command manager.
     * @param platform The platform.
     */
    public CommandManager(AnalysePlugin platform) {
        this.platform = platform;

        // Load built-in commands
        registerCommands("Analyse", Arrays.asList(new HelpCommand(platform), new SetupCommand(platform), new DebugCommand(platform)));
    }

    /**
     * Gets the platform.
     * @return The platform.
     */
    public AnalysePlugin getPlatform() {
        return platform;
    }

    /**
     * Registers the main command and tab completer.
     */
    public void register() {
        AnalyseCommand analyseCommand = new AnalyseCommand(this);
        PluginCommand pluginCommand = platform.getCommand("analyse");

        if(pluginCommand == null) {
            throw new RuntimeException("Analyse command not found.");
        }

        pluginCommand.setExecutor(analyseCommand);
        pluginCommand.setTabCompleter(analyseCommand);
    }

    /**
     * Registers a module's commands.
     * @param moduleName The name of the module.
     * @param commands The commands to register.
     */
    public void registerCommands(String moduleName, List<PlatformCommand> commands) {
        if (commandsByModule.containsKey(moduleName)) {
            platform.getLogger().warning("A module with the name '" + moduleName + "' is already registered. Skipping...");
            return;
        }

        Map<String, PlatformCommand> moduleCommands = new HashMap<>();
        commands.forEach(platformCommand -> {
            String commandName = platformCommand.getName();
            if (commandsByModule.values().stream()
                    .flatMap(commandsMap -> commandsMap.values().stream())
                    .map(PlatformCommand::getName)
                    .anyMatch(name -> name.equalsIgnoreCase(commandName))) {
                platform.getLogger().warning("A command with the name '" + commandName + "' is already registered in another module. Skipping...");
                return;
            }

            moduleCommands.put(commandName, platformCommand);
        });

        commandsByModule.put(moduleName, moduleCommands);
    }

    /**
     * Unregisters a module's commands.
     * @param moduleName The name of the module.
     */
    public void unregisterCommands(String moduleName) {
        commandsByModule.remove(moduleName);

        // Re-register the main command to update the tab completer
        register();
    }

    /**
     * Get a map of all registered commands grouped by module.
     * @return A map of all registered commands grouped by module.
     */
    public Map<String, Map<String, PlatformCommand>> getCommandsByModule() {
        return commandsByModule;
    }

}