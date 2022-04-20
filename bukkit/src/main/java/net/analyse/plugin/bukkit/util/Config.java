package net.analyse.plugin.bukkit.util;

import net.analyse.plugin.bukkit.AnalysePlugin;
import org.bukkit.configuration.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Config {

    private static final @NotNull Configuration config = AnalysePlugin.getPlugin(AnalysePlugin.class).getConfig();

    public static boolean DEBUG = config.getBoolean("debug", false);

    public static boolean USE_SERVER_FIRST_JOIN_DATE = config.getBoolean("use-server-first-join-date", false);

    public static List<String> EXCLUDED_PLAYERS = config.getStringList("excluded-players");

    public static List<String> ENABLED_STATS = config.getStringList("enabled-stats");

    public static int MIN_SESSION_DURATION = config.getInt("minimum-session-duration", 0);

    public static boolean ADVANCED_MODE = config.getBoolean("advanced.enabled", false);
    public static String REDIS_HOST = config.getString("advanced.redis.host", "127.0.0.1");
    public static int REDIS_PORT = config.getInt("advanced.redis.port", 6379);
    public static String REDIS_USERNAME = config.getString("advanced.redis.username");
    public static String REDIS_PASSWORD = config.getString("advanced.redis.password");
}
