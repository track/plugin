package net.analyse.plugin.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import net.analyse.plugin.request.object.PlayerStatistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor @Builder
public class PlayerSessionRequest {

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

    @Builder.Default private final List<PlayerStatistic> stats = new ArrayList<>();

    public String toJSON() {
        Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        return gson.toJson(this);
    }
}
