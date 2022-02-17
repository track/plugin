package net.analyse.plugin.commands;

import net.analyse.plugin.AnalysePlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class SubCommand {

    protected final AnalysePlugin plugin;
    private final String name;
    private final String permission;

    protected SubCommand(final @NotNull AnalysePlugin plugin, final @NotNull String name, final @NotNull String permission) {
        this.plugin = plugin;
        this.name = name;
        this.permission = permission;
    }

    public abstract void execute(final CommandSender player, final String[] args);

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
