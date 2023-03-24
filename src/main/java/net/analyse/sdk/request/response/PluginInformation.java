package net.analyse.sdk.request.response;

public class PluginInformation {
    private final String versionName;
    private final Integer versionNumber;
    private final String downloadUrl;

    public PluginInformation(String versionName, Integer versionNumber, String downloadUrl) {
        this.versionName = versionName;
        this.versionNumber = versionNumber;
        this.downloadUrl = downloadUrl;
    }

    public String getVersionName() {
        return versionName;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public String toString() {
        return "PluginInformation{" +
                "versionName='" + versionName + '\'' +
                ", versionNumber=" + versionNumber +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}
