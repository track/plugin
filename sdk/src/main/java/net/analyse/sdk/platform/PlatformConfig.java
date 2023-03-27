package net.analyse.sdk.platform;

import dev.dejvokep.boostedyaml.YamlDocument;

import java.util.List;
import java.util.UUID;

public class PlatformConfig {
    private final int configVersion;

    private List<UUID> excludedPlayers;
    private int minimumPlaytime;
    private boolean useServerFirstJoinedAt;

    private String serverToken;
    private String encryptionKey;

    private boolean debug;
    private boolean proxyMode;

    private YamlDocument yamlDocument;

    public PlatformConfig(int configVersion) {
        this.configVersion = configVersion;
    }

    public void setExcludedPlayers(List<UUID> excludedPlayers) {
        this.excludedPlayers = excludedPlayers;
    }

    public void setMinimumPlaytime(int minimumPlaytime) {
        this.minimumPlaytime = minimumPlaytime;
    }

    public void setUseServerFirstJoinedAt(boolean useServerFirstJoinedAt) {
        this.useServerFirstJoinedAt = useServerFirstJoinedAt;
    }

    public void setServerToken(String serverToken) {
        this.serverToken = serverToken;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public void setProxyMode(boolean proxyMode) {
        this.proxyMode = proxyMode;
    }

    public int getConfigVersion() {
        return configVersion;
    }

    public List<UUID> getExcludedPlayers() {
        return excludedPlayers;
    }

    public boolean isPlayerExcluded(UUID uuid) {
        return excludedPlayers.contains(uuid);
    }

    public int getMinimumPlaytime() {
        return minimumPlaytime;
    }

    public boolean shouldUseServerFirstJoinedAt() {
        return useServerFirstJoinedAt;
    }

    public String getServerToken() {
        return serverToken;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setDebugEnabled(boolean debug) {
        this.debug = debug;
    }

    public boolean hasDebugEnabled() {
        return debug;
    }

    public boolean hasProxyModeEnabled() {
        return proxyMode;
    }

    public void setYamlDocument(YamlDocument yamlDocument) {
        this.yamlDocument = yamlDocument;
    }

    public YamlDocument getYamlDocument() {
        return yamlDocument;
    }

}
