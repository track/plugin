package io.tebex.analytics.sdk.redis;


import io.tebex.analytics.sdk.redis.data.DataSet;
import io.tebex.analytics.sdk.redis.packet.Packet;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class Redis {

    private final Map<String, Consumer<Packet>> handlers = new HashMap<>();

    private final String host;
    private final String channel;
    private final String password;

    private final int port;

    private Jedis jedis;

    public Redis(@NotNull final String host, final int port, @NotNull final String channel, @NotNull final String password) {
        this.host = host;
        this.port = port;
        this.channel = channel;
        this.password = password;
    }

    public void connect() {
        this.jedis = new Jedis(this.host, this.port);
        this.jedis.auth(this.password);

        this.jedis.connect();

        new Thread(this::subscribe).start();
    }

    private void subscribe() {
        try (final Jedis subscriber = new Jedis(this.host, this.port)) {

            subscriber.auth(this.password);

            final JedisPubSub pubSub = new JedisPubSub() {
                @Override
                public void onPMessage(String pattern, String channel, String message) {
                    final Packet packet = Packet.fromJSON(message);

                    receive(packet);
                }
            };

            this.jedis.psubscribe(pubSub, this.channel);

        }
    }

    public void close() {
        this.jedis.close();
    }

    public void register(@NotNull final String header, @NotNull final Consumer<Packet> handler) {
        this.handlers.put(header, handler);
    }

    public void receive(@NotNull final Packet packet) {
        final Consumer<Packet> handle = this.handlers.get(packet.getHead());

        if (handle == null) {
            return;
        }

        handle.accept(packet);
    }


    public void publish(@NotNull final String head, @NotNull final DataSet... body) {

        try (final Jedis publisher = new Jedis(this.host, this.port)) {
            publisher.auth(this.password);

            publisher.publish(this.channel, new Packet(head, body).toJSON());
        }

    }


}