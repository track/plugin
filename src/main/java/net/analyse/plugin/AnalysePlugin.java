package net.analyse.plugin;

import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.IdentityHashingStrategy;
import net.analyse.plugin.commands.AnalyseCommand;
import net.analyse.plugin.event.ServerHeartbeatEvent;
import net.analyse.plugin.listener.PlayerActivityListener;
import net.analyse.plugin.util.Config;
import net.analyse.sdk.AnalyseSDK;
import net.analyse.sdk.exception.ServerNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static net.analyse.sdk.util.EncryptUtil.generateEncryptionKey;

public class AnalysePlugin extends JavaPlugin {
    private final Map<UUID, Date> activeJoinMap = new TCustomHashMap<>(new IdentityHashingStrategy<>());
    private final Map<UUID, String> playerDomainMap = new TCustomHashMap<>(new IdentityHashingStrategy<>());

    private AnalyseSDK core = null;

    private boolean setup;
    private boolean papiHooked;

    private String encryptionKey;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String serverToken = getConfig().getString("server.token");
        encryptionKey = getConfig().getString("encryption-key");

        setup = serverToken != null && !serverToken.isEmpty();

        papiHooked = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        getCommand("analyse").setExecutor(new AnalyseCommand(this));
        Bukkit.getPluginManager().registerEvents(new PlayerActivityListener(this), this);

        ServerHeartbeatEvent serverHeartBeatEvent = new ServerHeartbeatEvent(this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, serverHeartBeatEvent, 0, 20 * 10);

        if (encryptionKey == null || encryptionKey.isEmpty()) {
            encryptionKey = generateEncryptionKey(64);
            getConfig().set("encryption-key", encryptionKey);
            getLogger().info("Generated encryption key.");
            saveConfig();
        }

        if (!setup) {
            getLogger().info("Hey! I'm not yet set-up, please run the following command:");
            getLogger().info("/analyse setup <server-token>");
        } else {
            core = new AnalyseSDK(serverToken, encryptionKey);
            try {
                getLogger().info("Linked Analyse to " + core.getServer().getName() + ".");
            } catch (ServerNotFoundException e) {
                getLogger().warning("The server linked no longer exists.");
            }
        }
    }

    @Override
    public void onDisable() {
        activeJoinMap.clear();
        playerDomainMap.clear();
    }

    public Map<UUID, Date> getActiveJoinMap() {
        return activeJoinMap;
    }

    public Map<UUID, String> getPlayerDomainMap() {
        return playerDomainMap;
    }

    public boolean isSetup() {
        return setup;
    }

    public void setSetup(boolean setup) {
        this.setup = setup;
    }

    public String parse(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public boolean isPapiHooked() {
        return papiHooked;
    }

    public AnalyseSDK getCore() {
        return core;
    }

    public AnalyseSDK setup(String token) {
        core = new AnalyseSDK(token, encryptionKey);

        return core;
    }

    public void debug(String message) {
        if(Config.DEBUG) getLogger().info("DEBUG: " + message);
    }
}