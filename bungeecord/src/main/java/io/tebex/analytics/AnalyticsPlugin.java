package io.tebex.analytics;

import com.google.common.collect.Maps;
import io.tebex.analytics.event.PlayerJoinListener;
import io.tebex.analytics.event.PlayerQuitListener;
import io.tebex.analytics.event.PlayerSwitchListener;
import io.tebex.analytics.sdk.SDK;
import io.tebex.analytics.sdk.module.ModuleManager;
import io.tebex.analytics.sdk.obj.AnalysePlayer;
import io.tebex.analytics.sdk.platform.*;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public final class AnalyticsPlugin extends Plugin implements Platform {
    private final Map<UUID, String> playerDomains = Maps.newConcurrentMap();

    @Override
    public void onEnable() {
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new PlayerSwitchListener(this);
        getProxy().registerChannel("analyse:proxy");
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
        return getDataFolder();
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
        getLogger().log(level, message);
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
