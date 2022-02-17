package net.analyse.plugin.request;

public class ServerHeartbeatRequest extends AnalyseRequest {

    private final int players;

    public ServerHeartbeatRequest(final int players) {
        this.players = players;
    }

}
