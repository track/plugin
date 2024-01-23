package io.tebex.analytics.command;

import io.tebex.analytics.AnalyticsPlugin;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    private final AnalyticsPlugin platform;
    private final String name;
    private final String permission;

    public SubCommand(AnalyticsPlugin platform, String name, String permission) {
        this.platform = platform;
        this.name = name;
        this.permission = permission;
    }

    public abstract void execute(final CommandSender sender, final String[] args);

    public AnalyticsPlugin getPlatform() {
        return platform;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }
}
