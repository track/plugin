package net.analyse.plugin.manager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.command.AnalyseCommand;
import net.analyse.plugin.command.SubCommand;
import net.analyse.plugin.command.sub.DebugCommand;
import net.analyse.plugin.command.sub.SetupCommand;
import net.analyse.plugin.command.sub.StatsCommand;
import net.analyse.plugin.command.sub.TrackCommand;
import org.bukkit.command.PluginCommand;

import java.util.Map;

public class CommandManager {
    private final AnalysePlugin platform;
    private final Map<String, SubCommand> commands;

    public CommandManager(AnalysePlugin platform) {
        this.platform = platform;
        this.commands = Maps.newHashMap();
    }

    public void register() {
        ImmutableList.of(
                new SetupCommand(platform),
                new DebugCommand(platform),
                new StatsCommand(platform),
                new TrackCommand(platform)
        ).forEach(command -> {
            commands.put(command.getName(), command);
        });

        AnalyseCommand analyseCommand = new AnalyseCommand(this);
        PluginCommand pluginCommand = platform.getCommand("analyse");

        if(pluginCommand == null) {
            throw new RuntimeException("Analyse command not found.");
        }

        pluginCommand.setExecutor(analyseCommand);
        pluginCommand.setTabCompleter(analyseCommand);
    }

    public Map<String, SubCommand> getCommands() {
        return commands;
    }
}
