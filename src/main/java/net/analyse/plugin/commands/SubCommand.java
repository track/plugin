package net.analyse.plugin.commands;

import net.analyse.plugin.AnalysePlugin;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {

    protected final AnalysePlugin plugin;
    private final String name;
    private final String permission;

    protected SubCommand(AnalysePlugin plugin, String name, String permission) {
        this.plugin = plugin;
        this.name = name;
        this.permission = permission;
    }

    public abstract void execute(CommandSender player, String[] args);

    public AnalysePlugin getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }
}
