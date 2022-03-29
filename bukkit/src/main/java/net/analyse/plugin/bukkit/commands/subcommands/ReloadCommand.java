package net.analyse.plugin.bukkit.commands.subcommands;

import net.analyse.plugin.bukkit.AnalysePlugin;
import net.analyse.plugin.bukkit.commands.SubCommand;
import net.analyse.plugin.bukkit.util.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReloadCommand extends SubCommand {

    public ReloadCommand(final @NotNull AnalysePlugin plugin) {
        super(plugin, "reload", "analyse.reload");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        plugin.reloadConfig();

        FileConfiguration config = getPlugin().getConfig();

        Config.DEBUG = config.getBoolean("debug", false);
        Config.ENABLED_STATS = config.getStringList("enabled-stats");
        Config.MIN_SESSION_DURATION = config.getInt("minimum-session-duration", 0);

        List<UUID> excludedPlayers = plugin.getCore().getExcludedPlayers();
        excludedPlayers.clear();

        plugin.getConfig().getStringList("excluded.players").forEach(player -> {
            excludedPlayers.add(UUID.fromString(player));
        });

        sender.sendMessage(plugin.parse("&b[Analyse] &7Reloaded configuration file."));

        plugin.debug("Successfully reloaded!");
        plugin.debug("- Debug Enabled.");
        plugin.debug("- Enabled Stats: " + String.join(", ", Config.ENABLED_STATS));
        plugin.debug("- Excluded Players: " + excludedPlayers.stream().map(UUID::toString).collect(Collectors.joining(", ")));
        plugin.debug("- Min Session: " + Config.MIN_SESSION_DURATION);
    }
}
