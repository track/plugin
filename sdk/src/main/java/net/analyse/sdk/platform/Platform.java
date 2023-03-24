package net.analyse.sdk.platform;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.analyse.sdk.SDK;
import net.analyse.sdk.module.ModuleManager;
import net.analyse.sdk.obj.AnalysePlayer;
import net.analyse.sdk.request.exception.AnalyseException;
import net.analyse.sdk.request.response.PluginInformation;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static net.analyse.sdk.util.ResourceUtil.getBundledFile;

public interface Platform {
    PlatformType getType();
    SDK getSDK();

    Map<UUID, AnalysePlayer> getPlayers();

    /**
     * Get a player
     * @param uuid
     * @return
     */
    AnalysePlayer getPlayer(UUID uuid);

    /**
     * Get the directory where the plugin is running from.
     * @return The directory.
     */
    File getDirectory();

    boolean isSetup();
    void configure();
    void halt();

    String getVersion();

    default int getVersionNumber() {
        return Integer.parseInt(getVersion().replace(".", ""));
    }

    default PluginInformation getPluginInformation() throws AnalyseException {
        return getSDK().getPluginVersion(getType());
    }

    default boolean isOutdated() {
        try {
            return getVersionNumber() < getPluginInformation().getVersionNumber();
        } catch (AnalyseException e) {
            log(Level.WARNING, "Failed to check for updates: " + e.getMessage());
            return false;
        }
    }

    /**
     * Log a message to the console.
     * @param level The level of the message.
     * @param message The message to log.
     */
    void log(Level level, String message);

    /**
     * Log a normal message to the console.
     * @param message The message to log.
     */
    default void log(String message) {
        log(Level.INFO, message);
    }
    /**
     * Log a debug message to the console if debugging is enabled.
     * @param message The message to log.
     */
    default void debug(String message) {
        if (! getPlatformConfig().isDebugEnabled()) return;
        log("[DEBUG] " + message);
    }

    /**
     * Load configuration from file
     *
     * @return PlatformConfig
     */
    default PlatformConfig loadPlatformConfig() throws IOException {
        // Create and update the file
        YamlDocument configFile = YamlDocument.create(getBundledFile(this, getDirectory(), "config.yml"));

        PlatformConfig config = new PlatformConfig(configFile.getInt("config-version", 1));
        config.setYamlDocument(configFile);

        if(config.getConfigVersion() < 2) {
            throw new IOException("Your config is outdated. Please delete it and restart the server.");
        }

        config.setExcludedPlayers(configFile.getStringList("settings.excluded-players").stream().map(UUID::fromString).collect(Collectors.toList()));
        config.setMinimumPlaytime(configFile.getInt("settings.minimum-playtime", 0));
        config.setUseServerFirstJoinedAt(configFile.getBoolean("settings.use-server-playtime", false));

        config.setServerToken(configFile.getString("server.token"));
        config.setEncryptionKey(configFile.getString("server.encryption-key"));
        config.setDebugEnabled(configFile.getBoolean("debug", false));

        return config;
    }

    PlatformConfig getPlatformConfig();
    void setPlatformConfig(PlatformConfig config);

    PlatformTelemetry getTelemetry();
    ModuleManager getModuleManager();
}
