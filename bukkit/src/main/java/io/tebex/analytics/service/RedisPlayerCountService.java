package io.tebex.analytics.service;

import io.tebex.analytics.sdk.platform.Platform;
import io.tebex.analytics.sdk.platform.PlatformConfig;
import io.tebex.analytics.sdk.redis.Redis;
import io.tebex.analytics.sdk.redis.data.DataSet;
import io.tebex.analytics.sdk.service.PlayerCountService;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RedisPlayerCountService implements PlayerCountService {

    private static final String REQUEST = "ANALYSE_PLAYER_COUNT_REQUEST";
    private static final String RESPONSE = "ANALYSE_PLAYER_COUNT_RESPONSE";

    private final Map<UUID, Integer> servers = new HashMap<>();
    private final UUID session = UUID.randomUUID();

    private final Redis redis;

    public RedisPlayerCountService(final Platform platform) {
        final PlatformConfig config = platform.getPlatformConfig();

        this.redis = new Redis(config.getRedisHost(), config.getRedisPort(), "analyse", config.getRedisPassword());
        this.redis.connect();

        this.redis.register(REQUEST, packet -> this.redis.publish(RESPONSE,
                new DataSet("count", "" + Bukkit.getOnlinePlayers().size()),
                new DataSet("session", this.session.toString()))
        );

        this.redis.register(RESPONSE, packet -> this.servers.put(
                UUID.fromString((String) packet.getData("session").value()),
                Integer.parseInt((String) packet.getData("count").value()))
        );
    }

    public void terminate() {
        this.redis.close();
    }

    @Override
    public int getPlayerCount() {
        this.redis.publish(REQUEST, new DataSet("session", this.session.toString()));

        return this.servers.values().stream().mapToInt(Integer::intValue).sum();
    }

}
