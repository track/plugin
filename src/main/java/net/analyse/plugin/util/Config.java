package net.analyse.plugin.util;

import net.analyse.plugin.AnalysePlugin;
import org.bukkit.configuration.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Config {

    private static final @NotNull Configuration config = AnalysePlugin.getPlugin(AnalysePlugin.class).getConfig();

    public static boolean debug = config.getBoolean("debug", false);

    public static List<String> excludedPlayers = config.getStringList("excluded-players");

    public static List<String> enabledStats = config.getStringList("enabled-stats");

    public static int minSessionDuration = config.getInt("minimum-session-duration", 0);
}
