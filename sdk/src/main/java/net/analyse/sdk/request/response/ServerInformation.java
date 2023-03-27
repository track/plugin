package net.analyse.sdk.request.response;

import java.util.Date;
import java.util.UUID;

/**
 * Represents the information of a server.
 */
public class ServerInformation {
    private final String name;
    private final UUID uuid;
    private final Date createdAt;

    public ServerInformation(String name, UUID uuid, Date createdAt) {
        this.name = name;
        this.uuid = uuid;
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
