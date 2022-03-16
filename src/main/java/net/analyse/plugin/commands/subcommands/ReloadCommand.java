package net.analyse.plugin.commands.subcommands;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.commands.SubCommand;
import net.analyse.plugin.util.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends SubCommand {

    public ReloadCommand(final @NotNull AnalysePlugin plugin) {
        super(plugin, "reload", "analyse.reload");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        plugin.reloadConfig();

        FileConfiguration config = getPlugin().getConfig();

        Config.DEBUG = config.getBoolean("debug", false);
        Config.EXCLUDED_PLAYERS = config.getStringList("excluded-players");
        Config.ENABLED_STATS = config.getStringList("enabled-stats");
        Config.MIN_SESSION_DURATION = config.getInt("minimum-session-duration", 0);

        sender.sendMessage(plugin.parse("&b[Analyse] &7Reloaded configuration file."));

        plugin.debug("Successfully reloaded!");
        plugin.debug("- Debug Enabled.");
        plugin.debug("- Enabled Stats: " + String.join(", ", Config.ENABLED_STATS));
        plugin.debug("- Excluded Players: " + String.join(", ", Config.EXCLUDED_PLAYERS));
        plugin.debug("- Min Session: " + Config.MIN_SESSION_DURATION);
    }
}
