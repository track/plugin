package net.analyse.plugin.request.impl;

import net.analyse.plugin.request.AnalyseRequest;

public class ServerHeartbeatRequest extends AnalyseRequest {

    private final int players;

    public ServerHeartbeatRequest(final int players) {
        this.players = players;
    }

}
