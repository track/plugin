package net.analyse.sdk.response;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class GetPluginResponse {
    private String versionName;
    private Integer versionNumber;
    private String bukkitDownload;
    private String bungeeDownload;
    private String velocityDownload;

    public GetPluginResponse(@NotNull String versionName, @NotNull Integer versionNumber, @NotNull String bukkitDownload, @NotNull String bungeeDownload, @NotNull String velocityDownload) {
        this.versionName = versionName;
        this.versionNumber = versionNumber;
        this.bukkitDownload = bukkitDownload;
        this.bungeeDownload = bungeeDownload;
        this.velocityDownload = velocityDownload;
    }

    public String getVersionName() {
        return versionName;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public String getBukkitDownload() {
        return bukkitDownload;
    }

    public String getBungeeDownload() {
        return bungeeDownload;
    }

    public String getVelocityDownload() {
        return velocityDownload;
    }

    @Override
    public String toString() {
        return "GetPluginResponse{" +
                "versionName='" + versionName + '\'' +
                ", versionNumber=" + versionNumber +
                ", bukkitDownload='" + bukkitDownload + '\'' +
                ", bungeeDownload='" + bungeeDownload + '\'' +
                ", velocityDownload='" + velocityDownload + '\'' +
                '}';
    }
}
