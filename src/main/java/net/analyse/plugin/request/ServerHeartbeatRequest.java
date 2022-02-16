package net.analyse.plugin.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ServerHeartbeatRequest {
    private int players;

    public ServerHeartbeatRequest(int players) {
        this.players = players;
    }

    public String toJSON() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
