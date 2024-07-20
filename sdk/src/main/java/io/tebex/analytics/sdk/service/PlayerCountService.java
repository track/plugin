package io.tebex.analytics.sdk.service;

public interface PlayerCountService {

    /**
     * Retrieves the number of players currently online on the server.
     *
     * @return The number of players currently online.
     */
    int getPlayerCount();

}
