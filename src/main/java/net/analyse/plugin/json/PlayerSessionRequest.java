package net.analyse.plugin.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.analyse.plugin.json.object.PlayerStat;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PlayerSessionRequest {

    private final UUID uuid;
    private final String username;

    @SerializedName("joined_at")
    private final Date joinedAt;

    @SerializedName("quit_at")
    private final Date quitAt;

    private final List<PlayerStat> stats;

    public PlayerSessionRequest(UUID uuid, String username, Date joinedAt, Date quitAt, List<PlayerStat> stats) {
        this.uuid = uuid;
        this.username = username;
        this.joinedAt = joinedAt;
        this.quitAt = quitAt;
        this.stats = stats;
    }

    public String toJSON() {
        Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        return gson.toJson(this);
    }
}
