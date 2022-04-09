package net.analyse.plugin.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
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
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Plugin(
        id = "analyse",
        name = "Analyse",
        version = "1.1.0",
        description = "The proxy receiver for Analyse.",
        url = "https://analyse.net",
        authors = {"Analyse"}
)
public class AnalysePlugin {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;
    private JedisPooled redis;
    private AnalyseConfig config;

    @Inject
    public AnalysePlugin(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        logger.info("Enabling Analyse v" + getDescription().getVersion().orElse("Unknown"));

        try {
            this.config = loadConfig();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }

        loadRedis();
    }

    @Subscribe
    public void onJoin(PreLoginEvent event) {
        InetSocketAddress virtualDomain = event.getConnection().getVirtualHost().orElse(null);

        if(virtualDomain != null) {
            this.redis.set("analyse:connected_via:" + event.getUsername(), virtualDomain.getHostName());
        }
    }

    @Subscribe
    public void onLeave(DisconnectEvent event) {
        Player player = event.getPlayer();

        proxy.getScheduler()
            .buildTask(this, () -> {
                if(proxy.getPlayer(player.getUniqueId()).isPresent()) return;
                this.redis.del("analyse:connected_via:" + player.getUsername());
            })
            .delay(10L, TimeUnit.SECONDS)
            .schedule();
    }

    @Subscribe
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
                .setFile(getBundledFile("config.yml"))
                .build()
                .load();

        return new AnalyseConfig(configNode);
    }

    private File getBundledFile(String name) {
        File file = new File(dataDirectory.toFile(), name);

        if (!file.exists()) {
            dataDirectory.toFile().mkdir();
            try (InputStream in = AnalysePlugin.class.getResourceAsStream("/" + name)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public void loadRedis() {
        logger.info("Connecting to Redis under " + this.config.getHost() + ":" + config.getPort() + "..");
        redis = new JedisPooled(config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
    }

    PluginDescription getDescription() {
        return proxy.getPluginManager().getPlugin("analyse").map(PluginContainer::getDescription).orElse(null);
    }
}
