package net.analyse.plugin.bungee;

import ninja.leaping.configurate.ConfigurationNode;

public class AnalyseConfig {
    private final String host;
    private final Integer port;
    private final String username;
    private final String password;
    private final String uri;

    public AnalyseConfig(ConfigurationNode config) {
        ConfigurationNode redis = config.getNode("redis");

        host = redis.getNode("host").getString();
        port = redis.getNode("port").getInt(6379);
        username = redis.getNode("username").getString();
        password = redis.getNode("password").getString();
        uri = redis.getNode("uri").getString();
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUri() {
        return uri;
    }
}
