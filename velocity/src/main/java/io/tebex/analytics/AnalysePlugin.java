package io.tebex.analytics;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.tebex.analytics.event.PlayerJoinListener;
import io.tebex.analytics.event.PlayerQuitListener;
import io.tebex.analytics.event.PlayerSwitchListener;
import io.tebex.analytics.sdk.SDK;
import io.tebex.analytics.sdk.module.ModuleManager;
import io.tebex.analytics.sdk.obj.AnalysePlayer;
import io.tebex.analytics.sdk.platform.*;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Plugin(
        id = "analyse",
        name = "Analytics",
        version = "@VERSION@",
        description = "The Velocity plugin for Tebex Analytics.",
        url = "https://tebex.io",
        authors = {"Analytics"}
)
public final class AnalysePlugin implements Platform {
    private final Map<UUID, String> playerDomains = Maps.newConcurrentMap();
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;
    private final ChannelIdentifier channel;

    @Inject
    public AnalysePlugin(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.channel = MinecraftChannelIdentifier.create("analyse", "proxy");
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new PlayerSwitchListener(this);
        proxy.getChannelRegistrar().register(channel);
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public ChannelIdentifier getChannel() {
        return channel;
    }

    public Map<UUID, String> getPlayerDomains() {
        return playerDomains;
    }

    @Override
    public PlatformType getType() {
        return PlatformType.BUNGEECORD;
    }

    @Override
    public SDK getSDK() {
        return null;
    }

    @Override
    public Map<UUID, AnalysePlayer> getPlayers() {
        return null;
    }

    @Override
    public AnalysePlayer getPlayer(UUID uuid) {
        return null;
    }

    @Override
    public File getDirectory() {
        return dataDirectory.toFile();
    }

    @Override
    public boolean isSetup() {
        return false;
    }

    @Override
    public void configure() {

    }

    @Override
    public void halt() {

    }

    @Override
    public boolean isPluginEnabled(String plugin) {
        return false;
    }

    @Override
    public void loadModules() {

    }

    @Override
    public void loadModule(PlatformModule module) {

    }

    @Override
    public void unloadModules() {

    }

    @Override
    public void unloadModule(PlatformModule module) {

    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public void log(Level level, String message) {
        logger.log(level, message);
    }

    @Override
    public PlatformConfig getPlatformConfig() {
        return null;
    }

    @Override
    public void setPlatformConfig(PlatformConfig config) {

    }

    @Override
    public PlatformTelemetry getTelemetry() {
        return null;
    }

    @Override
    public ModuleManager getModuleManager() {
        return null;
    }
}
