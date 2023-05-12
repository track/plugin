package net.analyse.sdk.platform;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.analyse.sdk.SDK;
import net.analyse.sdk.module.ModuleManager;
import net.analyse.sdk.obj.AnalysePlayer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static net.analyse.sdk.util.ResourceUtil.getBundledFile;

/**
 * The Platform interface defines the base methods required for interacting with a server platform.
 * Implementations should provide functionality specific to their platform, such as Bukkit or Sponge.
 */
public interface Platform {
    /**
     * Gets the platform type.
     *
     * @return The PlatformType enum value representing the server platform.
     */
    PlatformType getType();

    /**
     * Gets the SDK instance associated with this platform.
     *
     * @return The SDK instance.
     */
    SDK getSDK();

    /**
     * Gets a map of all online players.
     *
     * @return A map with the UUID as the key and the AnalysePlayer object as the value.
     */
    Map<UUID, AnalysePlayer> getPlayers();

    /**
     * Gets an AnalysePlayer object by UUID.
     *
     * @param uuid The UUID of the player.
     * @return The AnalysePlayer object, or null if the player is not online.
     */
    AnalysePlayer getPlayer(UUID uuid);

    /**
     * Gets the directory where the plugin is running from.
     *
     * @return The directory.
     */
    File getDirectory();

    /**
     * Checks if the platform is set up and ready to use.
     *
     * @return True if the platform is set up, false otherwise.
     */
    boolean isSetup();

    /**
     * Configures the platform for use.
     */
    void configure();

    /**
     * Halts the platform and stops any ongoing tasks.
     */
    void halt();

    /**
     * Checks if a plugin is enabled on the platform.
     *
     * @param plugin The name of the plugin.
     * @return True if the plugin is enabled, false otherwise.
     */
    boolean isPluginEnabled(String plugin);

    /**
     * Loads all modules for the platform.
     */
    void loadModules();

    /**
     * Loads a specific module for the platform.
     *
     * @param module The module to load.
     */
    void loadModule(PlatformModule module);

    /**
     * Unloads all modules from the platform.
     */
    void unloadModules();

    /**
     * Unloads a specific module from the platform.
     *
     * @param module The module to unload.
     */
    void unloadModule(PlatformModule module);

    /**
     * Gets the version of the platform implementation.
     *
     * @return The version string.
     */
    String getVersion();

    /**
     * Converts the version string into a version number.
     *
     * @return The version number.
     */
    default int getVersionNumber() {
        return Integer.parseInt(getVersion().replace(".", ""));
    }

    /**
     * Logs a message to the console with the specified level.
     *
     * @param level   The level of the message.
     * @param message The message to log.
     */
    void log(Level level, String message);

    /**
     * Logs an informational message to the console.
     *
     * @param message The message to log.
     */
    default void log(String message) {
        log(Level.INFO, message);
    }

    /**
     * Logs a warning message to the console.
     *
     * @param message The message to log.
     */
    default void warning(String message) {
        log(Level.WARNING, message);
    }

    /**
     * Logs a debug message to the console if debugging is enabled in the platform configuration.
     *
     * @param message The message to log.
     */
    default void debug(String message) {
        if (! getPlatformConfig().hasDebugEnabled()) return;
        log("[DEBUG] " + message);
    }

    default YamlDocument initPlatformConfig() throws IOException {
        // Create and update the file
        return YamlDocument.create(getBundledFile(this, getDirectory(), "config.yml"));
    }
    /**
     * Loads the platform configuration from the file.
     *
     * @return The PlatformConfig instance representing the loaded configuration.
     * @throws IOException If there is an issue reading the configuration file or if the config version is outdated.
     */
    default PlatformConfig loadPlatformConfig(YamlDocument configFile) throws IOException {
        PlatformConfig config = new PlatformConfig(configFile.getInt("config-version", 1));
        config.setYamlDocument(configFile);

        if(config.getConfigVersion() < 2) {
            return config;
        }

        config.setExcludedPlayers(configFile.getStringList("settings.excluded-players").stream().map(UUID::fromString).collect(Collectors.toList()));
        config.setMinimumPlaytime(configFile.getInt("settings.minimum-playtime", 0));
        config.setUseServerFirstJoinedAt(configFile.getBoolean("settings.use-server-playtime", false));
        config.setProxyMode(configFile.getBoolean("settings.proxy-mode", false));
        config.setBedrockPrefix(configFile.getString("settings.bedrock-prefix"));

        if(configFile.getBoolean("hooks.placeholderapi.enabled")) {
            config.setEnabledPapiStatistics(configFile.getStringList("hooks.placeholderapi.enabled-stats"));
        }

        config.setServerToken(configFile.getString("server.token"));
        config.setEncryptionKey(configFile.getString("server.encryption-key"));
        config.setDebugEnabled(configFile.getBoolean("debug", false));

        return config;
    }

    /**
     * Exclude a player from being tracked by the plugin.
     *
     * @param uuid The UUID of the player to exclude.
     */
    default void excludePlayer(UUID uuid) {
        getExcludedPlayers().add(uuid);
    }

    /**
     * Include a player to be tracked by the plugin.
     *
     * @param uuid The UUID of the player to include.
     */
    default void includePlayer(UUID uuid) {
        getExcludedPlayers().remove(uuid);
    }

    /**
     * Checks if a player is excluded from being tracked by the plugin.
     *
     * @param uuid The UUID of the player to check.
     * @return True if the player is excluded, false otherwise.
     */
    default boolean isPlayerExcluded(UUID uuid) {
        return getExcludedPlayers().contains(uuid);
    }

    /**
     * Gets the excluded players list.
     *
     * @return The list of excluded players.
     */
    default List<UUID> getExcludedPlayers() {
        PlatformConfig config = getPlatformConfig();
        return config.getExcludedPlayers();
    }

    /**
     * Gets the current platform configuration.
     *
     * @return The PlatformConfig instance representing the current configuration.
     */
    PlatformConfig getPlatformConfig();

    /**
     * Sets the platform configuration.
     *
     * @param config The PlatformConfig instance to set as the current configuration.
     */
    void setPlatformConfig(PlatformConfig config);

    /**
     * Gets the platform telemetry instance.
     *
     * @return The PlatformTelemetry instance.
     */
    PlatformTelemetry getTelemetry();

    /**
     * Gets the module manager instance.
     *
     * @return The ModuleManager instance.
     */
    ModuleManager getModuleManager();
}
