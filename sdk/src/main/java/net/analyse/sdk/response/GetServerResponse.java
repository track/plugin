package net.analyse.sdk.response;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class GetServerResponse {
    private String name;
    private String uuid;
    private Instant createdAt;

    public GetServerResponse(@NotNull String name, @NotNull String uuid, @NotNull Instant createdAt) {
        this.name = name;
        this.uuid = uuid;
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
