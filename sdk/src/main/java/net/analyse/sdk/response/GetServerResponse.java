package net.analyse.sdk.response;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class GetServerResponse {
    private String name;
    private String uuid;
    private Instant createdAt;
    private Integer currentTeamQuota;
    private Integer teamQuotaLimit;

    public GetServerResponse(@NotNull String name, @NotNull String uuid, @NotNull Instant createdAt, @NotNull Integer currentTeamQuota, @NotNull Integer teamQuotaLimit) {
        this.name = name;
        this.uuid = uuid;
        this.createdAt = createdAt;
        this.currentTeamQuota = currentTeamQuota;
        this.teamQuotaLimit = teamQuotaLimit;
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

    public Integer getCurrentTeamQuota() {
        return currentTeamQuota;
    }

    public Integer getTeamQuotaLimit() {
        return teamQuotaLimit;
    }
}
