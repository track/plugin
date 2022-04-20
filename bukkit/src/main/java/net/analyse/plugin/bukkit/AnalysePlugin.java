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
import net.analyse.sdk.response.GetPluginResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPooled;

import java.util.*;
import java.util.stream.Collectors;

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

    private int incrementalVersion = Integer.parseInt(getDescription().getVersion().replace(".", ""));

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

        if (Config.ADVANCED_MODE) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> this.redis = new JedisPooled(Config.REDIS_HOST, Config.REDIS_PORT, Config.REDIS_USERNAME, Config.REDIS_PASSWORD));
            getLogger().info("Advanced mode is enabled.");
        }

        if(core != null) {
            List<UUID> excludedPlayers = core.getExcludedPlayers();
            getConfig().getStringList("excluded.players").forEach(player -> {
                excludedPlayers.add(UUID.fromString(player));
            });

            debug("Successfully booted!");
            debug("- Debug Enabled.");
            debug("- Use Server First Join Date: " + Config.USE_SERVER_FIRST_JOIN_DATE);
            debug("- Enabled Stats: " + String.join(", ", Config.ENABLED_STATS));
            debug("- Excluded Players: " + excludedPlayers.stream().map(UUID::toString).collect(Collectors.joining(", ")));
            debug("- Min Session: " + Config.MIN_SESSION_DURATION);
            debug("- Advanced Mode: " + Config.ADVANCED_MODE);

            GetPluginResponse corePluginVersion = core.getPluginVersion();
            if(corePluginVersion.getVersionNumber() > incrementalVersion) {
                getLogger().info(String.format("This server is running v%s, an outdated version of Analyse.", getDescription().getVersion()));
                getLogger().info(String.format("Download v%s at: %s", corePluginVersion.getVersionName(), corePluginVersion.getBukkitDownload()));
            }
        }

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
        final UUID playerUuid = player.getUniqueId();
        final String playerName = player.getName();
        final Date joinedAt = getActiveJoinMap().getOrDefault(playerUuid, null);
        final String playerIp = Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress();
        final Date quitAt = new Date();
        final long seconds = (quitAt.getTime()-joinedAt.getTime()) / 1000;
        final String domainConnected;

        debug(" ");
        debug("Preparing analytics for " + playerName + " (" + playerUuid + ")..");

        if(Config.ADVANCED_MODE && getRedis() != null) {
            domainConnected = getRedis().get("analyse:connected_via:" + playerName);
            debug(" - Connected from: '" + domainConnected + "' (Redis).");
        } else {
            domainConnected = getPlayerDomainMap().getOrDefault(playerUuid, null);
            debug(" - Connected from: '" + domainConnected + "' (Cache).");
        }
        debug(" - Joined at: " + joinedAt);
        debug(" - Player IP: " + playerIp);
        debug(" ");

        final List<PlayerStatistic> playerStatistics = core.getPlayerStatistics(player.getUniqueId());

        debug(" - Loaded stats: " + playerStatistics.size());
        for (PlayerStatistic playerStatistic : playerStatistics) {
            debug(" > Custom statistic %" + playerStatistic.getKey() + "% with value: " + playerStatistic.getValue());
        }

        if (isPapiHooked()) {
            for (String placeholder : Config.ENABLED_STATS) {
                String resolvedPlaceholder = PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%");

                if (!resolvedPlaceholder.equalsIgnoreCase("%" + placeholder + "%")) {
                    playerStatistics.add(new PlayerStatistic(placeholder, resolvedPlaceholder));
                    debug(" > PlaceholderAPI statistic %" + placeholder + "% with value: " + resolvedPlaceholder);
                } else {
                    debug(" > Skipping sending PlaceholderAPI statistic %" + placeholder + "% as it has no value.");
                }
            }
        }

        debug(" ");
        if(seconds >= Config.MIN_SESSION_DURATION) {
            try {
                getCore().sendPlayerSession(playerUuid, playerName, joinedAt, domainConnected, playerIp, Config.USE_SERVER_FIRST_JOIN_DATE ? new Date(player.getFirstPlayed()) : null, playerStatistics);
                debug("Sent player session data to Analyse!");
            } catch (ServerNotFoundException e) {
                setSetup(false);
                getLogger().warning("The server specified no longer exists.");
            }
        } else {
            debug("Skipping data as they haven't played for long enough (" + Config.MIN_SESSION_DURATION + " " + (Config.MIN_SESSION_DURATION == 1 ? "second" : "seconds") + " minimum).");
            debug("You can change this in the config.yml.");
        }
        debug(" ");

        clearPlayerCache(playerUuid);
    }

    private void clearPlayerCache(UUID playerUuid) {
        getPlayerDomainMap().remove(playerUuid);
        getActiveJoinMap().remove(playerUuid);
        core.getExcludedPlayers().remove(playerUuid);
        core.getPlayerStatistics(playerUuid).clear();
    }
}
