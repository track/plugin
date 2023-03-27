package net.analyse.sdk.platform;

public class PlatformTelemetry {
    private final String pluginVersion;
    private final String serverSoftware;
    private final String serverVersion;
    private final String javaVersion;
    private final String systemArch;
    private final boolean onlineMode;

    /**
     * Creates a new platform telemetry instance.
     * @param pluginVersion The version of the plugin.
     * @param serverSoftware The software of the server.
     * @param serverVersion The version of the server.
     * @param javaVersion The version of the java runtime.
     * @param systemArch The architecture of the system.
     * @param onlineMode Whether the server is in online mode.
     */
    public PlatformTelemetry(String pluginVersion, String serverSoftware, String serverVersion, String javaVersion, String systemArch, boolean onlineMode) {
        this.pluginVersion = pluginVersion;
        this.serverSoftware = serverSoftware;
        this.serverVersion = serverVersion;
        this.javaVersion = javaVersion;
        this.systemArch = systemArch;
        this.onlineMode = onlineMode;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public String getServerSoftware() {
        return serverSoftware;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public String getSystemArch() {
        return systemArch;
    }

    public boolean isOnlineMode() {
        return onlineMode;
    }

    /**
     * Returns a string representation of this object.
     * @return A string representation of this object.
     */
    @Override
    public String toString() {
        return "PlatformTelemetry{" +
                "pluginVersion='" + pluginVersion + '\'' +
                ", serverSoftware='" + serverSoftware + '\'' +
                ", serverVersion='" + serverVersion + '\'' +
                ", javaVersion='" + javaVersion + '\'' +
                ", systemArch='" + systemArch + '\'' +
                ", onlineMode=" + onlineMode +
                '}';
    }
}
