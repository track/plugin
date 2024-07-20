package io.tebex.analytics.sdk.redis.packet;

import com.google.gson.Gson;
import io.tebex.analytics.sdk.redis.data.DataSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Packet {

    private static final Gson GSON = new Gson();

    private final String head;
    private final DataSet[] body;

    public Packet(final String head, final DataSet... body) {
        this.head = head;
        this.body = body;
    }

    @NotNull
    public static Packet fromJSON(@NotNull final String json) {
        return Packet.GSON.fromJson(json, Packet.class);
    }

    @NotNull
    public String toJSON() {
        return Packet.GSON.toJson(this);
    }

    @Nullable
    public DataSet getData(@NotNull final String key) {

        for (@NotNull final DataSet data : this.body) {
            if (!data.key().equals(key)) {
                continue;
            }

            return data;
        }

        return null;
    }

    public DataSet[] getBody() {
        return this.body;
    }

    public String getHead() {
        return this.head;
    }

}