package net.analyse.plugin.command;

import net.analyse.plugin.AnalysePlugin;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    private final AnalysePlugin platform;
    private final String name;
    private final String permission;

    public SubCommand(AnalysePlugin platform, String name, String permission) {
        this.platform = platform;
        this.name = name;
        this.permission = permission;
    }

    public abstract void execute(final CommandSender sender, final String[] args);

    public AnalysePlugin getPlatform() {
        return platform;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }
}
