package net.analyse.plugin.bukkit;

import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.IdentityHashingStrategy;
import me.clip.placeholderapi.PlaceholderAPI;
import net.analyse.plugin.bukkit.commands.AnalyseCommand;
import net.analyse.plugin.bukkit.event.ServerHeartbeatEvent;
import net.analyse.plugin.bukkit.listener.PlayerActivityListener;
import net.analyse.plugin.bukkit.util.Config;
import net.analyse.sdk.AnalyseSDK;
import net.analyse.sdk.exception.ServerNotFoundException;
import net.analyse.sdk.request.object.PlayerStatistic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPooled;

import java.util.*;

import static net.analyse.plugin.bukkit.util.EncryptUtil.generateEncryptionKey;

public class AnalysePlugin extends JavaPlugin {
    private final Map<UUID, Date> activeJoinMap = new TCustomHashMap<>(new IdentityHashingStrategy<>());
    private final Map<UUID, String> playerDomainMap = new TCustomHashMap<>(new IdentityHashingStrategy<>());

    private AnalyseSDK core = null;

    private boolean setup;
    private boolean papiHooked;

    private String serverToken;
    private String encryptionKey;
    private JedisPooled redis = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        serverToken = getConfig().getString("server.token");
        encryptionKey = getConfig().getString("encryption-key");

        setup = serverToken != null && !serverToken.isEmpty();

        papiHooked = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        getCommand("analyse").setExecutor(new AnalyseCommand(this));
        Bukkit.getPluginManager().registerEvents(new PlayerActivityListener(this), this);

        ServerHeartbeatEvent serverHeartBeatEvent = new ServerHeartbeatEvent(this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, serverHeartBeatEvent, 0, 20 * 10);

        if (Config.ADVANCED_MODE) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> this.redis = new JedisPooled(Config.REDIS_HOST, Config.REDIS_PORT));
        }

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

        debug("Successfully booted!");
        debug("- Debug Enabled.");
        debug("- Enabled Stats: " + String.join(", ", Config.ENABLED_STATS));
        debug("- Excluded Players: " + String.join(", ", Config.EXCLUDED_PLAYERS));
        debug("- Min Session: " + Config.MIN_SESSION_DURATION);
        debug("- Advanced Mode: " + Config.ADVANCED_MODE);
    }

    @Override
    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            sendPlayerSessionInformation(player, true);
        }
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

    public JedisPooled getRedis() {
        return redis;
    }

    public void debug(String message) {
        if(Config.DEBUG) getLogger().info("DEBUG: " + message);
    }

    public void sendPlayerSessionInformation(Player player, Boolean performingShutdown) {
        if (Config.EXCLUDED_PLAYERS.contains(player.getUniqueId().toString())) return;

        if(performingShutdown) {
            sendData(player);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
           sendData(player);
        });
    }

    private void sendData(Player player) {
        final List<PlayerStatistic> playerStatistics = new ArrayList<>();

        if (isPapiHooked()) {
            for (String placeholder : Config.ENABLED_STATS) {
                String resolvedPlaceholder = PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%");

                if (!resolvedPlaceholder.equalsIgnoreCase("%" + placeholder + "%")) {
                    playerStatistics.add(new PlayerStatistic(placeholder, resolvedPlaceholder));
                    debug("Sending %" + placeholder + "% to Analyse with value: " + resolvedPlaceholder);
                } else {
                    debug("Skipping sending %" + placeholder + "% to Analyse as it has no value.");
                }
            }
        }

        final UUID playerUuid = player.getUniqueId();
        final String playerName = player.getName();
        final Date joinedAt = getActiveJoinMap().getOrDefault(playerUuid, null);
        final String playerIp = Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress();
        final Date quitAt = new Date();
        final long seconds = (quitAt.getTime()-joinedAt.getTime()) / 1000;
        final String domainConnected;

        if(Config.ADVANCED_MODE && getRedis() != null) {
            domainConnected = getRedis().get("analyse:connected_via:" + playerName);
            debug(playerName + " connected from '" + domainConnected + "' (from Redis).");
        } else {
            domainConnected = getPlayerDomainMap().getOrDefault(playerUuid, null);
            debug(playerName + " connected from '" + domainConnected + "' (from Cache).");
        }

        if(seconds >= Config.MIN_SESSION_DURATION) {
            try {
                getCore().sendPlayerSession(playerUuid, playerName, joinedAt, domainConnected, playerIp, playerStatistics);
                debug(String.format("%s (%s) disconnected, who joined at %s and connected %s with IP of %s", playerName, playerUuid, joinedAt, (domainConnected != null ? "via " + domainConnected : "directly"), playerIp));
            } catch (ServerNotFoundException e) {
                setSetup(false);
                getLogger().warning("The server specified no longer exists.");
            }
        } else {
            debug("Skipping sending " + playerName + "'s data as they haven't played for long enough.");
            debug("Your current threshold is set to " + Config.MIN_SESSION_DURATION + " " + (Config.MIN_SESSION_DURATION == 1 ? "second" : "seconds") + " minimum.");
        }

        getActiveJoinMap().remove(player.getUniqueId());
        getPlayerDomainMap().remove(player.getUniqueId());
    }
}
