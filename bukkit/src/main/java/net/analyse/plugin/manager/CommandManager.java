package net.analyse.plugin.manager;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.command.AnalyseCommand;
import net.analyse.plugin.command.sub.DebugCommand;
import net.analyse.plugin.command.sub.SetupCommand;
import net.analyse.sdk.platform.command.PlatformCommand;
import org.bukkit.command.PluginCommand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {
    private final AnalysePlugin platform;
    private final Map<String, PlatformCommand> commands = new HashMap<>();

    public CommandManager(AnalysePlugin platform) {
        this.platform = platform;

        // Load built-in commands
        registerCommands(Arrays.asList(new SetupCommand(platform), new DebugCommand(platform)));
    }

    public void register() {
        AnalyseCommand analyseCommand = new AnalyseCommand(this);
        PluginCommand pluginCommand = platform.getCommand("analyse");

        if(pluginCommand == null) {
            throw new RuntimeException("Analyse command not found.");
        }

        pluginCommand.setExecutor(analyseCommand);
        pluginCommand.setTabCompleter(analyseCommand);
    }

    public void registerCommands(List<PlatformCommand> commands) {
        commands.forEach(platformCommand -> {
            if (this.commands.containsKey(platformCommand.getName())) {
                platform.getLogger().warning("A command with the name '" + platformCommand.getName() + "' is already registered. Skipping...");
                return;
            }

            this.commands.put(platformCommand.getName(), platformCommand);
        });
    }

    public Map<String, PlatformCommand> getCommands() {
        return commands;
    }
}