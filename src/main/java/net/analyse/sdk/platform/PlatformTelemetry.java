package net.analyse.sdk.platform;

public class PlatformTelemetry {
    private final String pluginVersion;
    private final String serverSoftware;
    private final String serverVersion;
    private final String javaVersion;
    private final String systemArch;
    private final boolean onlineMode;

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
