package io.tebex.analytics.manager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import io.tebex.analytics.AnalyticsPlugin;
import io.tebex.analytics.command.AnalyticsCommand;
import io.tebex.analytics.command.SubCommand;
import io.tebex.analytics.command.sub.*;
import org.bukkit.command.PluginCommand;

import java.util.Map;

public class CommandManager {
    private final AnalyticsPlugin platform;
    private final Map<String, SubCommand> commands;

    public CommandManager(AnalyticsPlugin platform) {
        this.platform = platform;
        this.commands = Maps.newHashMap();
    }

    public void register() {
        ImmutableList.of(
                new SetupCommand(platform),
                new DebugCommand(platform),
                new StatsCommand(platform),
                new TrackCommand(platform),
                new ReloadCommand(platform)
        ).forEach(command -> {
            commands.put(command.getName(), command);
        });

        AnalyticsCommand analyticsCommand = new AnalyticsCommand(this);
        PluginCommand pluginCommand = platform.getCommand("analytics");

        if(pluginCommand == null) {
            throw new RuntimeException("Analytics command not found.");
        }

        pluginCommand.setExecutor(analyticsCommand);
        pluginCommand.setTabCompleter(analyticsCommand);
    }

    public Map<String, SubCommand> getCommands() {
        return commands;
    }

    public AnalyticsPlugin getPlatform() {
        return platform;
    }
}
