package net.analyse.sdk.request.impl;

import net.analyse.sdk.request.AnalyseRequest;

public class ServerHeartbeatRequest extends AnalyseRequest {

    private final int players;

    public ServerHeartbeatRequest(final int players) {
        this.players = players;
    }

}
