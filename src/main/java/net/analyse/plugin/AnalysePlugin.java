package net.analyse.plugin;

import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.IdentityHashingStrategy;
import net.analyse.plugin.commands.AnalyseCommand;
import net.analyse.plugin.event.ServerHeartbeatEvent;
import net.analyse.plugin.listener.PlayerActivityListener;
import net.analyse.plugin.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static net.analyse.plugin.util.EncryptUtil.generateEncryptionKey;

public class AnalysePlugin extends JavaPlugin {
    private final Map<UUID, Date> activeJoinMap = new TCustomHashMap<>(new IdentityHashingStrategy<>());
    private final Map<UUID, String> playerDomainMap = new TCustomHashMap<>(new IdentityHashingStrategy<>());

    private boolean setup;
    private String encryptionKey;
    private ServerHeartbeatEvent serverHeartBeatEvent;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        setup = getConfig().getString("server-token") != null && !getConfig().getString("server-token").isEmpty();
        encryptionKey = getConfig().getString("encryption-key");

        getCommand("analyse").setExecutor(new AnalyseCommand(this));
        Bukkit.getPluginManager().registerEvents(new PlayerActivityListener(this), this);

        serverHeartBeatEvent = new ServerHeartbeatEvent(this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> serverHeartBeatEvent.run(), 0, 20 * 10);

        if(encryptionKey == null || encryptionKey.isEmpty()) {
            encryptionKey = generateEncryptionKey(64);
            getConfig().set("encryption-key", encryptionKey);
            getLogger().info("Generated encryption key.");
            saveConfig();
        }

        if(!setup) {
            getLogger().info("Hey! I'm not yet set-up, please run the following command:");
            getLogger().info("/analyse setup <server-token>");
        }
    }

    @Override
    public void onDisable() {
        activeJoinMap.clear();
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
}
