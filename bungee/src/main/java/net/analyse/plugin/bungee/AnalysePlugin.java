package net.analyse.plugin.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import redis.clients.jedis.JedisPooled;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class AnalysePlugin extends Plugin implements Listener {
    private JedisPooled redis;
    private AnalyseConfig config;

    @Override
    public void onEnable() {
        this.getLogger().info("Enabling Analyse");

        try {
            config = loadConfig();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }

        loadRedis();

        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onJoin(LoginEvent event) {
        InetSocketAddress virtualDomain = event.getConnection().getVirtualHost();

        if (virtualDomain != null) {
            String hostName = virtualDomain.getHostName();
            if(hostName.contains("._minecraft._tcp.")) {
                hostName = hostName.split("._minecraft._tcp.", 2)[1];
            }

            redis.set("analyse:connected_via:" + event.getConnection().getName(), hostName);
        }
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final String name = event.getPlayer().getName();

        this.getProxy().getScheduler()
                .schedule(this, () -> {
                    if (player != null && player.isConnected()) return;
                    redis.del("analyse:connected_via:" + name);
                }, 10L, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onReload(ProxyReloadEvent event) {
        reloadConfig();
    }

    public void reloadConfig() {
        try {
            config = loadConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AnalyseConfig loadConfig() throws Exception {
        TypeSerializerCollection serializerCollection = TypeSerializerCollection.create();

        ConfigurationOptions options = ConfigurationOptions.defaults()
                .withSerializers(serializerCollection);

        ConfigurationNode configNode = YAMLConfigurationLoader.builder()
                .setDefaultOptions(options)
                .setFile(getBundledFile())
                .build()
                .load();

        return new AnalyseConfig(configNode);
    }

    private File getBundledFile() {
        File file = new File(this.getDataFolder(), "config.yml");

        if (!file.exists()) {
            this.getDataFolder().mkdir();
            try (InputStream in = AnalysePlugin.class.getResourceAsStream("/" + "config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public void loadRedis() {
        if(config.getUri() != null) {
            getLogger().info("Connecting to Redis under " + config.getUri() + "..");
            redis = new JedisPooled(config.getUri());
        } else {
            getLogger().info("Connecting to Redis under " + config.getHost() + ":" + config.getPort() + "..");
            redis = new JedisPooled(config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
        }
    }
}
