package net.analyse.plugin;

import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.IdentityHashingStrategy;
import net.analyse.plugin.event.PlayerJoinListener;
import net.analyse.plugin.event.PlayerQuitListener;
import net.analyse.plugin.event.ServerLoadListener;
import net.analyse.plugin.manager.CommandManager;
import net.analyse.plugin.manager.HeartbeatManager;
import net.analyse.sdk.Analyse;
import net.analyse.sdk.SDK;
import net.analyse.sdk.module.ModuleManager;
import net.analyse.sdk.obj.AnalysePlayer;
import net.analyse.sdk.platform.*;
import net.analyse.sdk.request.exception.AnalyseException;
import net.analyse.sdk.request.exception.ServerNotFoundException;
import net.analyse.sdk.request.response.PluginInformation;
import net.analyse.sdk.request.response.ServerInformation;
import net.analyse.sdk.util.StringUtil;
import net.analyse.sdk.util.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AnalysePlugin extends JavaPlugin implements Platform {
    private SDK sdk;
    private PlatformConfig config;
    private Map<UUID, AnalysePlayer> players;
    private boolean setup;
    private HeartbeatManager heartbeatManager;
    private ModuleManager moduleManager;

    @Override
    public void onEnable() {
        // initialise SDK.
        Analyse.init(this);

        try {
            // Load the platform config file.
            config = loadPlatformConfig();
        } catch (IOException e) {
            log(Level.WARNING, "Failed to load config: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        players = new TCustomHashMap<>(new IdentityHashingStrategy<>());

        // Initialise managers.
        heartbeatManager = new HeartbeatManager(this);
        new CommandManager(this).register();

        sdk = new SDK(this, config.getServerToken());

        if (config.getServerToken() != null && !config.getServerToken().isEmpty()) {
            try {
                ServerInformation serverInformation = sdk.getServerInformation();
                log("Connected to '" + serverInformation.getName() + "'.");
                configure();
            } catch (AnalyseException e) {
                log(Level.WARNING, "Failed to get server information: " + e.getMessage());
            } catch (ServerNotFoundException e) {
                log(Level.WARNING, "Failed to connect. Please double-check your server key or run the setup command again.");
            }
        } else {
            log(Level.WARNING, "Welcome to Analyse! It seems like this is a new setup.");
            log(Level.WARNING, "To get started, please use the 'analyse setup <key>' command in the console.");
        }

        registerEvents(new PlayerJoinListener(this));
        registerEvents(new PlayerQuitListener(this));

        debug("Debug mode enabled. Type 'analyse debug' to disable.");
        debug("Telemetry: " + getTelemetry());

        try {
            PluginInformation corePluginVersion = getPluginInformation();
            if (VersionUtil.isNewerVersion(getVersion(), corePluginVersion.getVersionName())) {
                log(Level.WARNING, String.format("New version available (v%s). You are currently running v%s.", corePluginVersion.getVersionName(), getDescription().getVersion()));
                log(Level.WARNING, "Download the latest version at: " + corePluginVersion.getDownloadUrl());
            } else {
                log("You are running the latest version of Analyse.");
            }
        } catch (AnalyseException e) {
            getLogger().warning("Failed to get plugin information: " + e.getMessage());
        }

        try {
            Class.forName("org.bukkit.event.server.ServerLoadEvent");
            registerEvents(new ServerLoadListener(this));
        } catch (final ClassNotFoundException ignored) {
            Bukkit.getScheduler().runTaskLater(this, this::loadModules, 1);
        }
    }

    @Override
    public void onDisable() {
        unloadModules();
    }

    @Override
    public void loadModules() {
        try {
            log("Loading modules..");
            moduleManager = new ModuleManager(this);
            List<PlatformModule> modules = moduleManager.load();

            modules.forEach(this::loadModule);

            log(modules.size() + " " + StringUtil.pluralise(modules.size(), "module", "modules") + " loaded.");
        } catch (Exception e) {
            log(Level.WARNING, "Failed to load modules: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void loadModule(PlatformModule module) {
        if (module instanceof Listener) {
            getServer().getPluginManager().registerEvents((Listener) module, this);
        }
        moduleManager.register(module);
        moduleManager.getModules().add(module);
    }

    @Override
    public void unloadModules() {
        Iterator<PlatformModule> iterator = moduleManager.getModules().iterator();

        while(iterator.hasNext()) {
            PlatformModule module = iterator.next();
            unloadModule(module);
            iterator.remove();
        }
    }

    @Override
    public void unloadModule(PlatformModule module) {
        if (module instanceof Listener) {
            HandlerList.unregisterAll((Listener) module);
        }
        moduleManager.unregister(module);
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

    @Override
    public PlatformType getType() {
        return PlatformType.BUKKIT;
    }

    @Override
    public SDK getSDK() {
        return sdk;
    }

    @Override
    public Map<UUID, AnalysePlayer> getPlayers() {
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
}
