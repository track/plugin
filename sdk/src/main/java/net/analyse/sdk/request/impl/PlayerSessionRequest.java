package net.analyse.sdk.request.impl;

import com.google.gson.annotations.SerializedName;
import net.analyse.sdk.request.AnalyseRequest;
import net.analyse.sdk.request.object.PlayerStatistic;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PlayerSessionRequest extends AnalyseRequest {

    private final UUID uuid;
    private final String name;

    @SerializedName("joined_at")
    private final Date joinedAt;

    @SerializedName("quit_at")
    private final Date quitAt;

    private final String domain;

    @SerializedName("ip_address")
    private String ipAddress;

    @SerializedName("country")
    private String country;

    private List<PlayerStatistic> stats;

    public PlayerSessionRequest(
            final @NotNull UUID uuid,
            final @NotNull String name,
            final @NotNull Date joinedAt,
            final @NotNull Date quitAt,
            final @NotNull String domain,
            final @NotNull String ipAddress,
            final @NotNull String country,
            final @NotNull List<PlayerStatistic> stats
    ) {
        this.uuid = uuid;
        this.name = name;
        this.joinedAt = joinedAt;
        this.quitAt = quitAt;
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.country = country;
        this.stats = stats;
    }

}
