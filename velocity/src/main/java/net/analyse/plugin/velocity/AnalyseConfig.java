package net.analyse.plugin.velocity;

import ninja.leaping.configurate.ConfigurationNode;

public class AnalyseConfig {
    private String host;
    private Integer port;

    public AnalyseConfig(ConfigurationNode config) {
        ConfigurationNode redis = config.getNode("redis");

        this.host = redis.getNode("host").getString();
        this.port = redis.getNode("port").getInt(6379);
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }
}
