package net.analyse.plugin.velocity;

import ninja.leaping.configurate.ConfigurationNode;

public class AnalyseConfig {
    private String host;
    private Integer port;
    private String username;
    private String password;

    public AnalyseConfig(ConfigurationNode config) {
        ConfigurationNode redis = config.getNode("redis");

        this.host = redis.getNode("host").getString();
        this.port = redis.getNode("port").getInt(6379);
        this.username = redis.getNode("username").getString();
        this.password = redis.getNode("password").getString();
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
}
