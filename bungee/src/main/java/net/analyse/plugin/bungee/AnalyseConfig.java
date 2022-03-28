package net.analyse.plugin.bungee;

import ninja.leaping.configurate.ConfigurationNode;

public class AnalyseConfig {
    private final String host;
    private final Integer port;

    public AnalyseConfig(ConfigurationNode config) {
        ConfigurationNode redis = config.getNode("redis");

        host = redis.getNode("host").getString();
        port = redis.getNode("port").getInt(6379);
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }
}
