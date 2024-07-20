package io.tebex.analytics.sdk.platform;

import dev.dejvokep.boostedyaml.YamlDocument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The PlatformConfig class holds the configuration for the Analytics SDK.
 * It contains settings related to excluded players, minimum playtime, and various other options.
 */
public class PlatformConfig {
    private final int configVersion;

    private List<UUID> excludedPlayers;
    private int minimumPlaytime;
    private boolean useServerFirstJoinedAt;

    private List<String> enabledPapiStatistics;
    private boolean bedrockFloodgateHook;

    private String serverToken;
    private String encryptionKey;
    private String bedrockPrefix;

    private boolean redisEnabled;
    private int redisPort;
    private String redisHost;
    private String redisPassword;

    private boolean debug;
    private boolean proxyMode;

    private YamlDocument yamlDocument;

    /**
     * Creates a PlatformConfig instance with the provided configuration version.
     *
     * @param configVersion The configuration version.
     */
    public PlatformConfig(int configVersion) {
        this.configVersion = configVersion;
    }

    /**
     * Sets the list of excluded players.
     *
     * @param excludedPlayers The list of excluded player UUIDs.
     */
    public void setExcludedPlayers(List<UUID> excludedPlayers) {
        this.excludedPlayers = excludedPlayers != null ? excludedPlayers : Collections.emptyList();
    }

    /**
     * Sets the minimum playtime for players.
     *
     * @param minimumPlaytime The minimum playtime in minutes.
     */
    public void setMinimumPlaytime(int minimumPlaytime) {
        this.minimumPlaytime = minimumPlaytime;
    }

    /**
     * Sets whether to use the server's first joined timestamp for players.
     *
     * @param useServerFirstJoinedAt Whether to use the server's first joined timestamp.
     */
    public void setUseServerFirstJoinedAt(boolean useServerFirstJoinedAt) {
        this.useServerFirstJoinedAt = useServerFirstJoinedAt;
    }

    /**
     * Sets the list of enabled PAPI statistics.
     *
     * @param enabledPapiStatistics The list of enabled PAPI statistics.
     */
    public void setEnabledPapiStatistics(List<String> enabledPapiStatistics) {
        this.enabledPapiStatistics = enabledPapiStatistics != null ? enabledPapiStatistics : Collections.emptyList();
    }

    /**
     * Sets the optional Bedrock Floodgate API hook
     *
     * @param bedrockFloodgateHook Whether we should use the Floodgate API to detect Bedrock players instead.
     */
    public void setBedrockFloodgateHook(boolean bedrockFloodgateHook) {
        this.bedrockFloodgateHook = bedrockFloodgateHook;
    }

    /**
     * Sets the server token.
     *
     * @param serverToken The server token.
     */
    public void setServerToken(String serverToken) {
        this.serverToken = serverToken;
    }

    /**
     * Sets the encryption key.
     *
     * @param encryptionKey The encryption key.
     */
    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    /**
     * Sets the prefix for Bedrock Edition players.
     *
     * @param bedrockPrefix The prefix for Bedrock Edition players.
     */
    public void setBedrockPrefix(String bedrockPrefix) {
        this.bedrockPrefix = bedrockPrefix;
    }

    /**
     * Sets whether the server is in proxy mode.
     *
     * @param proxyMode Whether the server is in proxy mode.
     */
    public void setProxyMode(boolean proxyMode) {
        this.proxyMode = proxyMode;
    }

    /**
     * Sets whether the debug mode is enabled.
     *
     * @param debug Whether the debug mode is enabled.
     */
    public void setDebugEnabled(boolean debug) {
        this.debug = debug;
    }

    /**
     * Sets the YAML document for this configuration.
     *
     * @param yamlDocument The YAML document.
     */
    public void setYamlDocument(YamlDocument yamlDocument) {
        this.yamlDocument = yamlDocument;
    }

    /**
     * Returns the configuration version.
     *
     * @return The configuration version.
     */
    public int getConfigVersion() {
        return configVersion;
    }

    /**
     * Returns the list of excluded players.
     *
     * @return The list of excluded player UUIDs.
     */
    public List<UUID> getExcludedPlayers() {
        return excludedPlayers;
    }

    /**
     * Checks if a player is excluded based on their UUID.
     *
     * @param uuid The player's UUID.
     * @return True if the player is excluded, false otherwise.
     */
    public boolean isPlayerExcluded(UUID uuid) {
        return excludedPlayers.contains(uuid);
    }

    /**
     * Returns the minimum playtime for players.
     *
     * @return The minimum playtime in minutes.
     */
    public int getMinimumPlaytime() {
        return minimumPlaytime;
    }

    /**
     * Checks if the server should use its first joined timestamp for players.
     *
     * @return True if the server should use its first joined timestamp, false otherwise.
     */
    public boolean shouldUseServerFirstJoinedAt() {
        return useServerFirstJoinedAt;
    }

    /**
     * Returns the server token.
     *
     * @return The server token.
     */
    public String getServerToken() {
        return serverToken;
    }

    /**
     * Returns the encryption key.
     *
     * @return The encryption key.
     */
    public String getEncryptionKey() {
        return encryptionKey;
    }

    /**
     * Returns the prefix for Bedrock Edition players.
     *
     * @return The prefix for Bedrock Edition players.
     */
    public String getBedrockPrefix() {
        return bedrockPrefix;
    }

    /**
     * Returns whether Analytics should hook into Floodgate.
     *
     * @return The {@link Boolean} that represents whether Analytics should hook into Floodgate's API or not.
     */
    public boolean isBedrockFloodgateHook() {
        return bedrockFloodgateHook;
    }

    /**
     * Checks if the debug mode is enabled.
     *
     * @return True if the debug mode is enabled, false otherwise.
     */
    public boolean hasDebugEnabled() {
        return debug;
    }

    /**
     * Checks if the server is in proxy mode.
     *
     * @return True if the server is in proxy mode, false otherwise.
     */
    public boolean hasProxyModeEnabled() {
        return proxyMode;
    }

    /**
     * Returns the list of enabled PAPI statistics.
     *
     * @return The list of enabled PAPI statistics.
     */
    public List<String> getEnabledPapiStatistics() {
        return enabledPapiStatistics;
    }

    /**
     * Checks if Redis integration is enabled.
     *
     * @return true if Redis integration is enabled, false otherwise.
     */
    public boolean isRedisEnabled() {
        return redisEnabled;
    }

    /**
     * Enables or disables Redis integration.
     *
     * @param enabled true to enable Redis integration, false to disable.
     */
    public void setRedisEnabled(final boolean enabled) {
        this.redisEnabled = enabled;
    }

    /**
     * Retrieves the Redis server port.
     *
     * @return The port number as an int.
     */
    public int getRedisPort() {
        return redisPort;
    }

    /**
     * Sets the Redis server port.
     *
     * @param port The port number as an int.
     */
    public void setRedisPort(final int port) {
        this.redisPort = port;
    }

    /**
     * Retrieves the Redis server host address.
     *
     * @return The host address as a String.
     */
    public String getRedisHost() {
        return redisHost;
    }

    /**
     * Sets the Redis server host address.
     *
     * @param host The host address as a String.
     */
    public void setRedisHost(final String host) {
        this.redisHost = host;
    }

    /**
     * Retrieves the Redis server authentication password.
     *
     * @return The password as a String.
     */
    public String getRedisPassword() {
        return redisPassword;
    }

    /**
     * Sets the Redis server authentication password.
     *
     * @param password The password as a String.
     */
    public void setRedisPassword(final String password) {
        this.redisPassword = password;
    }

    /**
     * Returns the YAML document for this configuration.
     *
     * @return The YAML document.
     */
    public YamlDocument getYamlDocument() {
        return yamlDocument;
    }
}
