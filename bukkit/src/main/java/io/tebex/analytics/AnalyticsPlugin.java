package io.tebex.analytics;

import com.google.common.collect.Maps;
import dev.dejvokep.boostedyaml.YamlDocument;
import io.tebex.analytics.event.PlayerJoinListener;
import io.tebex.analytics.event.PlayerQuitListener;
import io.tebex.analytics.event.ProxyMessageListener;
import io.tebex.analytics.event.ServerLoadListener;
import io.tebex.analytics.hook.FloodgateHook;
import io.tebex.analytics.hook.PlaceholderAPIExpansionHook;
import io.tebex.analytics.hook.PlaceholderAPIStatisticsHook;
import io.tebex.analytics.manager.CommandManager;
import io.tebex.analytics.manager.HeartbeatManager;
import io.tebex.analytics.sdk.platform.*;
import io.tebex.analytics.sdk.request.exception.RateLimitException;
import io.tebex.analytics.sdk.Analytics;
import io.tebex.analytics.sdk.SDK;
import io.tebex.analytics.sdk.exception.ServerNotFoundException;
import io.tebex.analytics.sdk.module.ModuleManager;
import io.tebex.analytics.sdk.obj.AnalysePlayer;
import io.tebex.analytics.sdk.util.StringUtil;
import io.tebex.analytics.sdk.util.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.morepaperlib.scheduling.GracefulScheduling;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Bukkit platform.
 */
public final class AnalyticsPlugin extends JavaPlugin implements Platform {
    private SDK sdk;
    private PlatformConfig config;
    private ConcurrentMap<UUID, AnalysePlayer> players;
    private boolean setup;
    private boolean proxyModeEnabled;
    private HeartbeatManager heartbeatManager;
    private ModuleManager moduleManager;
    private ProxyMessageListener proxyMessageListener;
    private FloodgateHook floodgateHook;
    private MorePaperLib morePaperLib;

    /**
     * Starts the Bukkit platform.
     */
    @Override
    public void onEnable() {
        // Bind SDK.
        Analytics.init(this);

        try {
            // Load the platform config file.
            YamlDocument configYaml = initPlatformConfig();
            config = loadPlatformConfig(configYaml);

            if(config.getConfigVersion() == 1) {
                log("Detected a legacy config file. Attempting to migrate..");

                // Get config values
                String serverToken = getConfig().getString("server.token");
                boolean papiEnabled = getConfig().getBoolean("hooks.papi");
                List<String> papiStatistics = getConfig().getStringList("enabled-stats");
                int minSessionDuration = getConfig().getInt("minimum-session-duration");
                boolean useServerFirstJoinDate = getConfig().getBoolean("use-server-first-join-date");
                String encryptionKey = getConfig().getString("encryption-key");
                List<String> excludedPlayers = getConfig().getStringList("excluded.players");
                boolean proxyModeEnabled = getConfig().getBoolean("advanced.enabled");

                // Delete the old config file.
                File file = new File(getDataFolder(), "config.yml");
                if(! file.delete()) {
                    throw new IOException("Please delete the old config file and restart the server.");
                }

                // Create a new config file.
                configYaml = initPlatformConfig();
                config = loadPlatformConfig(configYaml);

                // Set the values.
                config.getYamlDocument().set("hooks.placeholderapi.enabled", papiEnabled);
                config.getYamlDocument().set("hooks.floodgate.enabled", Bukkit.getPluginManager().isPluginEnabled("floodgate"));
                config.getYamlDocument().set("hooks.placeholderapi.enabled-stats", papiStatistics);

                config.getYamlDocument().set("settings.excluded-players", excludedPlayers);
                config.getYamlDocument().set("settings.minimum-playtime", minSessionDuration);
                config.getYamlDocument().set("settings.use-server-playtime", useServerFirstJoinDate);
                config.getYamlDocument().set("settings.proxy-mode", proxyModeEnabled);

                config.getYamlDocument().set("server.token", serverToken);
                config.getYamlDocument().set("server.encryption-key", encryptionKey);

                // Save the config file.
                config.getYamlDocument().save();

                // Reload the platform config file.
                config = loadPlatformConfig(configYaml);

                log("Successfully migrated your config file.");
            }
        } catch (IOException e) {
            log(Level.WARNING, "Failed to load config: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        players = Maps.newConcurrentMap();

        // Initialise Managers.
        heartbeatManager = new HeartbeatManager(this);
        new CommandManager(this).register();

        // Initialise SDK.
        sdk = new SDK(this, config.getServerToken());

        // Check if the server has been set up.
        if (config.getServerToken() != null && !config.getServerToken().isEmpty()) {
            sdk.getServerInformation().thenAccept(serverInformation -> {
                log("Connected to " + serverInformation.getName() + ".");
                configure();
            }).exceptionally(ex -> {
                Throwable cause = ex.getCause();

                if(cause instanceof ServerNotFoundException) {
                    warning("Failed to connect. Please double-check your server key or run the setup command again.");
                    this.halt();
                } else {
                    warning("Failed to get server information: " + cause.getMessage());
                    cause.printStackTrace();
                }

                return null;
            });
        } else {
            log(Level.WARNING, "Welcome to Tebex Analytics! It seems like this is a new setup.");
            log(Level.WARNING, "To get started, please use the 'analytics setup <key>' command in the console.");
        }

        // Register events.
        registerEvents(new PlayerJoinListener(this));
        registerEvents(new PlayerQuitListener(this));

        debug("Debug mode enabled. Type 'analytics debug' to disable.");

        sdk.getPluginVersion(getType()).thenAccept(pluginInformation -> {
            if (VersionUtil.isNewerVersion(getVersion(), pluginInformation.getVersionName())) {
                log(Level.WARNING, String.format("New version available (v%s). You are currently running v%s.", pluginInformation.getVersionName(), getDescription().getVersion()));
                log(Level.WARNING, "Download the latest version at: " + pluginInformation.getDownloadUrl());
                log(Level.WARNING, "View the changelog at: https://analy.se/plugin/releases/tag/" + pluginInformation.getVersionName());
            } else {
                log("You are running the latest version of Analytics.");
            }
        }).exceptionally(ex -> {
            Throwable cause = ex.getCause();
            log(Level.WARNING, "Failed to get plugin version: " + cause.getMessage());

            if(cause instanceof ServerNotFoundException) {
                this.halt();
            } else {
                cause.printStackTrace();
            }

            return null;
        });

        proxyMessageListener = new ProxyMessageListener(this);
        proxyModeEnabled = config.hasProxyModeEnabled();

        if(proxyModeEnabled) {
            proxyMessageListener.register();
        }

        if(config.isBedrockFloodgateHook() && Bukkit.getPluginManager().isPluginEnabled("floodgate")) {
            log("Hooked into Floodgate.");
            floodgateHook = new FloodgateHook();
        }

        morePaperLib = new MorePaperLib(this);

        // Load modules.
        try {
            Class.forName("org.bukkit.event.server.ServerLoadEvent");
            registerEvents(new ServerLoadListener(this));
        } catch (final ClassNotFoundException ignored) {
            getScheduler().globalRegionalScheduler().runDelayed(this::loadModules, 1);
        }
    }

    @Override
    public void onDisable() {
        unloadModules();
    }

    @Override
    public void loadModules() {
        try {
            moduleManager = new ModuleManager(this);
            List<PlatformModule> modules = moduleManager.load();

            if(! modules.isEmpty()) {
                log("Loading modules..");
                modules.forEach(this::loadModule);

                log(modules.size() + " " + StringUtil.pluralise(modules.size(), "module", "modules") + " loaded.");
            }
        } catch (Exception e) {
            log(Level.WARNING, "Failed to load modules: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void loadModule(PlatformModule module) {
        if(moduleManager == null) return;
        if (module instanceof Listener) {
            getServer().getPluginManager().registerEvents((Listener) module, this);
        }
        moduleManager.register(module);
        moduleManager.getModules().add(module);
    }

    @Override
    public void unloadModules() {
        if(moduleManager == null) return;
        Iterator<PlatformModule> iterator = moduleManager.getModules().iterator();

        while(iterator.hasNext()) {
            PlatformModule module = iterator.next();
            unloadModule(module);
            iterator.remove();
        }
    }

    @Override
    public void unloadModule(PlatformModule module) {
        if(moduleManager == null) return;
        if (module instanceof Listener) {
            HandlerList.unregisterAll((Listener) module);
        }
        moduleManager.unregister(module);
    }

    public GracefulScheduling getScheduler() {
        return morePaperLib.scheduling();
    }

    /**
     * Registers the specified listener with the plugin manager.
     * @param l the listener to register
     */
    public <T extends Listener> void registerEvents(T l) {
        getServer().getPluginManager().registerEvents(l, this);
    }

    public HeartbeatManager getHeartbeatManager() {
        return heartbeatManager;
    }

    public FloodgateHook getFloodgateHook() {
        return floodgateHook;
    }

    @Override
    public PlatformType getType() {
        return PlatformType.BUKKIT;
    }

    @Override
    public SDK getSDK() {
        return sdk;
    }

    @Override
    public ConcurrentMap<UUID, AnalysePlayer> getPlayers() {
        return players;
    }

    @Override
    public AnalysePlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    @Override
    public File getDirectory() {
        return getDataFolder();
    }

    @Override
    public boolean isSetup() {
        return setup;
    }

    @Override
    public void configure() {
        setup = true;
        heartbeatManager.start();

        if(isSetup()) {
            sdk.sendTelemetry().thenAccept(telemetry -> {
                debug("Sent telemetry data.");
            }).exceptionally(ex -> {
                Throwable cause = ex.getCause();

                log(Level.WARNING, "Failed to send telemetry: " + cause.getMessage());

                if(cause instanceof RateLimitException) {
                    log(Level.WARNING, "Please wait a few minutes and try again.");
                } else {
                    cause.printStackTrace();
                }

                return null;
            });
        }
    }

    @Override
    public void halt() {
        setup = false;
        heartbeatManager.stop();
    }

    @Override
    public boolean isPluginEnabled(String plugin) {
        return Bukkit.getServer().getPluginManager().isPluginEnabled(plugin);
    }

    @Override
    public void log(Level level, String message) {
        getLogger().log(level, message);
    }

    @Override
    public PlatformConfig getPlatformConfig() {
        return config;
    }

    @Override
    public void setPlatformConfig(PlatformConfig config) {}

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public PlatformTelemetry getTelemetry() {
        String serverVersion = getServer().getVersion();

        Pattern pattern = Pattern.compile("MC: (\\d+\\.\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(serverVersion);
        if (matcher.find()) {
            serverVersion = matcher.group(1);
        }

        return new PlatformTelemetry(
                getVersion(),
                getServer().getName(),
                serverVersion,
                System.getProperty("java.version"),
                System.getProperty("os.arch"),
                getServer().getOnlineMode()
        );
    }

    @Override
    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public boolean isProxyModeEnabled() {
        return proxyModeEnabled;
    }

    public void setProxyModeEnabled(boolean proxyModeEnabled) {
        this.proxyModeEnabled = proxyModeEnabled;
    }

    public ProxyMessageListener getProxyMessageListener() {
        return proxyMessageListener;
    }

    public void updatePlaceholderAPIStatistics(Player player, Map<String, Object> stats) {
        if(! getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) return;
        if(getPlatformConfig().getEnabledPapiStatistics() == null) return;

        for (String statistic : getPlatformConfig().getEnabledPapiStatistics()) {
            String value = PlaceholderAPIStatisticsHook.getStatistic(player, statistic);
            if(value.isEmpty()) continue;
            stats.put(statistic, value);
        }
    }

    public void sendMessage(CommandSender sender, String message) {
        String str = ChatColor.translateAlternateColorCodes('&', "&b[Analytics] &7" + message);

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.stripColor(str));
            return;
        }

        sender.sendMessage(str);
    }
}
